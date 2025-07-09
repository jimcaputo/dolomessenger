package com.example.dolomessenger


import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

object SpeechRecognitionManager: RecognitionListener {

    private lateinit var transcriptionViewModel: TranscriptionViewModel

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private lateinit var audioManager: AudioManager


    private var active: Boolean = false

    fun create(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0)
        audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0)
        audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_MUTE, 0)
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0)
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        speechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(this)
    }

    fun start(transcriptionViewModel: TranscriptionViewModel) {
        this.transcriptionViewModel = transcriptionViewModel
        this.transcriptionViewModel.reset()
        speechRecognizer.startListening(speechIntent)
        active = true
    }

    fun stop() {
        speechRecognizer.stopListening()
        active = false
    }

    override fun onError(errorCode: Int) {
        val tvm = transcriptionViewModel

        // http://developer.android.com/reference/android/speech/SpeechRecognizer.html
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> tvm.appendError("Audio recording error")
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> tvm.appendError("Insufficient permissions")
            SpeechRecognizer.ERROR_NETWORK -> tvm.appendError("Network error")
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> tvm.appendError("Network timeout")
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> tvm.appendError("RecognitionService busy")
            SpeechRecognizer.ERROR_SERVER -> tvm.appendError("Error from server")
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> tvm.appendError("No speech input")
            SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> tvm.appendError("Language not available (ie downloaded yet)")
            SpeechRecognizer.ERROR_CLIENT -> { tvm.appendError("Client side error")
                // TODO: Unclear what causes us to land here intermittently, but we will need to restart.
                // And it will need to triggered by the user, so need to build UI flow for this.
                // Unfortunately attempting to stop / start causes a bad loop. So we'll need to stop and
                // ask the user to manually restart the app.
                //speechRecognizer.stopListening()
                //speechRecognizer.startListening(speechIntent)
            }
            SpeechRecognizer.ERROR_NO_MATCH -> {
                if (active) {
                    speechRecognizer.startListening(speechIntent)
                }
            }
            else -> tvm.appendError("Error Code: $errorCode")
        }
    }

    override fun onResults(bundle: Bundle) {
        val words = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        if (words != null) {
            transcriptionViewModel.onTranscription(words[0])

            if (active) {
                speechRecognizer.startListening(speechIntent)
            }
        }
    }

    override fun onReadyForSpeech(bundle: Bundle) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(v: Float) {
    }

    override fun onBufferReceived(bytes: ByteArray) {
    }

    override fun onEndOfSpeech() {
    }

    override fun onPartialResults(bundle: Bundle) {
        // TODO - do we want to attempt faster detection, particularly for the Help Phrase?
    }

    override fun onEvent(i: Int, bundle: Bundle) {
    }
}

