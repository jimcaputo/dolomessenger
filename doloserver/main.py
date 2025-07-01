import firebase_admin
from firebase_admin import credentials, messaging, firestore
from flask import Flask, request
import functions_framework

app = Flask(__name__)

# Initialize Firebase Admin SDK
cred = credentials.Certificate("google-services.json")
firebase_admin.initialize_app(cred)
db = firestore.client()


@app.route("/")
def home():
    return "Hello DoLo"


@app.route("/listtokens", methods=["GET"])
def listTokens():
    response = ""
    try:
        instances = db.collection("instances")
        docs = instances.stream()
        for doc in docs:
            response += f"{doc.id} => {doc.to_dict()}<br>"
    except Exception as e:
        print(f"Error updating database: {e}")
    return response


@app.route("/updatetoken", methods=["POST"])
def updateToken():
    try:
        json = request.get_json()
        doc_ref = db.collection("instances").document(json['instance'])
        doc_ref.set({"token": json['token'], "timestamp": json['timestamp']})
        response = "Success"
    except Exception as e:
        response = f"Error updating database: {e}"
    return f"{{'Status': '{response}''}}"


@app.route("/sendmessage", methods=["POST"])
def sendMessage():
    try:
        json = request.get_json()
  
        # Construct the message
        message = messaging.Message(
            notification = messaging.Notification(
                title = json['title'],
                body = json['body'],
            ),
            token = json['to']
        )

        # Send the message
        response = messaging.send(message)
        response = "Successfully sent message"
    except Exception as e:
        response = f"Error sending message: {e}"
    return f"{{'Status': '{response}'}}"


@app.route("/broadcast", methods=['POST'])
def broadcast():
    try:
        response = ""
        json = request.get_json()

        instances = db.collection("instances")
        docs = instances.stream()
        for doc in docs:
            # Construct the message
            message = messaging.Message(
                notification = messaging.Notification(
                    title = json['title'],
                    body = json['body'],
                ),
                token = doc.to_dict()['token']
            )
            # Send the message
            messaging.send(message)
        response = "Successfully broadcast message"
    except Exception as e:
        response = f"Error sending message: {e}"
    return f"{{'Status': '{response}'}}"


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=int(8080))
