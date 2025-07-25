package com.example.dolomessenger

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReceiverViewModel : ViewModel() {
    var notifications by mutableStateOf("")

    init {
        viewModelScope.launch {
            val token = Firebase.messaging.token.await()
            DoLoServerAPI.updateToken(token, this@ReceiverViewModel)
        }
    }

    fun appendNotification(notification: String) {
        if (notifications != "") notifications += "\n"
        notifications += notification
    }
}