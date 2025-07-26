package com.dolphinbaycapital.dolomessenger

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
            val body = message.notification?.body ?: "<empty message>"
            NotificationManager.rvm.appendNotification(body)
        }
    }
}