package com.dolphinbaytech.dolomessenger

object NotificationManager {
    private var rvm: ReceiverViewModel? = null

    fun create(rvm: ReceiverViewModel) {
        NotificationManager.rvm = rvm
    }

    fun appendNotification(message: String) {
        rvm?.appendNotification(message)
    }
}