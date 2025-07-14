package com.example.dolomessenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReceiverViewModel : ViewModel() {

    init {
        viewModelScope.launch {
            val token = Firebase.messaging.token.await()
            DoLoServerAPI.updateToken(token)
        }
    }
}