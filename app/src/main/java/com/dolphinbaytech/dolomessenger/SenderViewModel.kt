package com.dolphinbaytech.dolomessenger

import android.content.Context
import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel

class SenderViewModel : ViewModel() {
    var helpPhrase by mutableStateOf("I need help Don")
    var helpPhraseTriggered: Boolean = false
    var activationPhrase by mutableStateOf("Send Message")
    private var activationPhraseTimestamp: Long = 0     // Time of last mention of the ActivationPhrase
    private val activationPhraseCaptureTime = 5
    var messages by mutableStateOf("Activated messages")
    var transcription by mutableStateOf("Full transcription")
    var errors by mutableStateOf("Errors")

    private lateinit var sharedPref: SharedPreferences

    fun create(context: Context) {
        sharedPref = context.getSharedPreferences("DoLo Messenger", Context.MODE_PRIVATE)
        helpPhrase = sharedPref.getString("Help Phrase", "I need help Don") ?: "I need help Don"
        activationPhrase = sharedPref.getString("Activation Phrase", "Send Message") ?: "Send Message"
    }

    fun updateHelpPhrase(helpPhrase: String) {
        this.helpPhrase = helpPhrase
        sharedPref.edit {
            putString("Help Phrase", helpPhrase)
            apply()
        }
    }

    fun updateActivationPhrase(activationPhrase: String) {
        this.activationPhrase = activationPhrase
        sharedPref.edit {
            putString("Activation Phrase", activationPhrase)
            apply()
        }
    }

    fun onTranscription(transcription: String) {
        appendTranscription(transcription)

        if (activationPhraseTimestamp > 0  &&
            activationPhraseTimestamp + activationPhraseCaptureTime < System.currentTimeMillis() / 1000L) {
            // This can occur after the ActivationPhrase is captured, but no message follows it. See comment below
            appendMessage(transcription)
            activationPhraseTimestamp = 0   // Reset so that follow on transcription text goes through normal flow
        }
        else if (transcription.contains(helpPhrase, ignoreCase = true)) {
            DoLoServerAPI.broadcastMessage(helpPhrase, this)

            helpPhraseTriggered = true
            object : CountDownTimer(5000, 5000) {
                override fun onTick(millisUntilFinished: Long) { }
                override fun onFinish() { helpPhraseTriggered = false }
            }.start()
        }
        else if (transcription.indexOf(activationPhrase, ignoreCase = true) > -1) {
            val startIndex = transcription.indexOf(activationPhrase, ignoreCase = true) + activationPhrase.length
            val message = transcription.substring(startIndex)
            if (message != "") {
                appendMessage(message)
            }
            else {
                // We can land here when the activationPhrase was triggered, but the subsequent message was not
                // communicated quickly enough. So we will assume that the next message within
                // activationPhraseCaptureTime seconds is what we need to send as a notification.
                activationPhraseTimestamp = System.currentTimeMillis() / 1000L
            }

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

        DoLoServerAPI.broadcastMessage(message, this)
    }

    private fun appendTranscription(text: String) {
        if (transcription != "") transcription += "\n"
        transcription += text
    }
}