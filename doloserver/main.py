import firebase_admin
from firebase_admin import credentials, messaging, firestore
from flask import Flask, request
import functions_framework
from datetime import datetime

app = Flask(__name__)

# Initialize Firebase Admin SDK
cred = credentials.Certificate("google-services.json")
firebase_admin.initialize_app(cred)
db = firestore.client()


@app.route("/")
def home():
    return "Hello DoLo"

@app.route("/listtokens", methods=["GET"])
def http_listtokens():
    response = ""
    try:
        instances = db.collection("instances")
        docs = instances.stream()
        for doc in docs:
            response += f"{doc.id} => {doc.to_dict()}<br>"
    except Exception as e:
        print(f"Error updating database: {e}")
    return response

def update_token(uuid, device, token):
    try:
        doc_ref = db.collection("instances").document(uuid)
        doc_ref.set({"device": device, "token": token, "timestamp": datetime.now().timestamp()})
        response = "Success"
    except Exception as e:
        response = f"Error updating database: {e}"
    return response

@app.route("/updatetoken", methods=["POST"])
def http_updatetoken():
    try:
        json = request.get_json()
        response = update_token(json['uuid'], json['device'], json['token'])
    except Exception as e:
        response = f"Error updating database: {e}"
    return f"{{'Status': '{response}'}}"

def send_message(uuid, device, token, title, body):
    try:
        message = messaging.Message(
            notification = messaging.Notification(
                title = title,
                body = body,
            ),
            token = token
        )
        messaging.send(message)
        response = "Success"
    except (firebase_admin.exceptions.InvalidArgumentError, messaging.UnregisteredError) as e:
        # Handle the unregistered token by erasing it from the database
        update_token(uuid, device, "")
        response = "Success"
    except Exception as e:
        response = f"UUID: {uuid}. Error sending message to: {e}"
    return response

@app.route("/broadcast", methods=['POST'])
def http_broadcast():
    try:
        json = request.get_json()
        instances = db.collection("instances")
        docs = instances.stream()
        for doc in docs:
            if doc.to_dict()['token'] != "":
                response = send_message(
                    doc.id,
                    doc.to_dict()['device'],
                    doc.to_dict()['token'],
                    json['title'],
                    json['body'])
                if response != "Success":
                    return f"{{'Status': '{response}'}}"
        response = "Success"
    except Exception as e:
        response = f"Error broadcasting message: {e}"

    return f"{{'Status': '{response}'}}"


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=int(8080))
