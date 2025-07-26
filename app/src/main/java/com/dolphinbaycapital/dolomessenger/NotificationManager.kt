package com.dolphinbaycapital.dolomessenger

object NotificationManager {
    lateinit var rvm: ReceiverViewModel

    fun create(rvm: ReceiverViewModel) {
        this.rvm = rvm
    }
}