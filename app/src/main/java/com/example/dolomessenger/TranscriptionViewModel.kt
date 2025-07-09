package com.example.dolomessenger

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TranscriptionViewModel : ViewModel() {
    var helpPhrase by mutableStateOf("I need help Donald")
    var helpPhraseTriggered by mutableStateOf(false)
    var activationPhrase by mutableStateOf("Hey Donald")
    var messages by mutableStateOf("Activated messages")
    var transcription by mutableStateOf("Full transcription")
    var errors by mutableStateOf("Speech recognition errors")

    fun onTranscription(transcription: String) {
        appendTranscription(transcription)

        if (transcription.contains(helpPhrase, ignoreCase = true)) {
            helpPhraseTriggered = true

            object : CountDownTimer(5000, 5000) {
                override fun onTick(millisUntilFinished: Long) { }
                override fun onFinish() { helpPhraseTriggered = false }
            }.start()
        }
        else if (transcription.indexOf(activationPhrase, ignoreCase = true) > -1) {
            val startIndex = transcription.indexOf(activationPhrase, ignoreCase = true) + activationPhrase.length
            appendMessage(transcription.substring(startIndex))
        }
    }

    fun reset() {
        messages = ""
        transcription = ""
        errors = ""
    }

    fun appendError(error: String) {
        if (errors != "") errors += "\n"
        errors += error
    }

    private fun appendMessage(message: String) {
        if (messages != "") messages += "\n"
        messages += message

        val localToken = Firebase.messaging.token.await()


    }

    private fun appendTranscription(text: String) {
        if (transcription != "") transcription += "\n"
        transcription += text
    }
}