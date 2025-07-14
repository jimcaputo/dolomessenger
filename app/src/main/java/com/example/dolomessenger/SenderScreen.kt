package com.example.dolomessenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SenderScreen(
    svm: SenderViewModel,
    onStateChange: (String) -> Unit
) {
    var captureActive by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Controls(svm) { captureActive = it }
        HelpPhraseContent(svm)
        ActivationPhraseContent(svm)
        TranscriptionContent(svm)
        ErrorContent(svm)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = { onStateChange("Activation") },
            enabled = !captureActive
        ) {
            Text(text = "Switch App Mode")
        }
    }
}

@Composable
fun Controls(
    svm: SenderViewModel,
    onCaptureActiveChange: (Boolean) -> Unit
) {
    var buttonCapture by rememberSaveable { mutableStateOf("Start Capture") }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                if (buttonCapture == "Start Capture") {
                    svm.reset()
                    SpeechRecognitionManager.start(svm)
                    buttonCapture = "Stop Capture"
                    onCaptureActiveChange(true)
                } else {
                    SpeechRecognitionManager.stop()
                    buttonCapture = "Start Capture"
                    onCaptureActiveChange(false)
                }
            }
        ) {
            Text(
                text = buttonCapture
            )
        }
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                svm.reset()
            }
        ) {
            Text(
                text = "Reset Transcription"
            )
        }
    }
}

@Composable
fun HelpPhraseContent(svm: SenderViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = svm.helpPhrase,
            onValueChange = { svm.helpPhrase = it },
            label = { Text("Enter Help Phrase") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if (svm.helpPhraseTriggered) {
            Text(color = Color.Red, text = "Help Phrase Triggered")
        }
    }
}

@Composable
fun ActivationPhraseContent(svm: SenderViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = svm.activationPhrase,
            onValueChange = { svm.activationPhrase = it },
            label = { Text("Enter Activation Phrase") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TranscriptionContent(svm: SenderViewModel) {
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = svm.messages)
    }
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = svm.transcription)
    }
}

@Composable
fun ErrorContent(svm: SenderViewModel) {
    Column(
        modifier = Modifier
            .padding(vertical = 70.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = svm.errors)
    }
}



@Preview(showBackground = true)
@Composable
fun SenderScreenPreview() {
    val svm: SenderViewModel = viewModel()
    SenderScreen(svm) { }
}