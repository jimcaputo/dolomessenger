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
    var helpPhrase by mutableStateOf("I need help Donald")
    var helpPhraseTriggered by mutableStateOf(false)
    var activationPhrase by mutableStateOf("Send Message")
    var activationPhraseTriggered: Boolean = false
    private val ACTIVATION_PHRASE_CAPTURE_TIME = 5000L      // 5 seconds

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

        if (activationPhraseTriggered) {
            appendMessage(transcription)
            activationPhraseTriggered = false
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
                // ACTIVATION_PHRASE_CAPTURE_TIME seconds is what we need to send as a notification.

                activationPhraseTriggered = true    // Signals that the next message is intended for notification

                object : CountDownTimer(ACTIVATION_PHRASE_CAPTURE_TIME, ACTIVATION_PHRASE_CAPTURE_TIME) {
                    override fun onTick(millisUntilFinished: Long) { }
                    override fun onFinish() { activationPhraseTriggered = false }
                }.start()
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
        messages += message.trim()

        DoLoServerAPI.broadcastMessage(message.trim(), this)
    }

    private fun appendTranscription(text: String) {
        if (transcription != "") transcription += "\n"
        transcription += text
    }
}