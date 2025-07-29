package com.dolphinbaytech.dolomessenger

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        DoLoServerAPI.updateToken(token, null)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.notification != null) {
            val body = message.notification?.body ?: ""
            if (body != "") {
                NotificationManager.appendNotification(body)
            }
        }
    }
}