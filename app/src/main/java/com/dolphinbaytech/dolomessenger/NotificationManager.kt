package com.dolphinbaytech.dolomessenger

object NotificationManager {
    lateinit var rvm: ReceiverViewModel

    fun create(rvm: ReceiverViewModel) {
        NotificationManager.rvm = rvm
    }
}