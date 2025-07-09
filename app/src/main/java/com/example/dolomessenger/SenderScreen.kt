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
    tvm: TranscriptionViewModel = viewModel(),
    onStateChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(onClick = {
            onStateChange("Activation")
        }) {
            Text(text = "Deactivate")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Controls(tvm)
        HelpPhraseContent(tvm)
        ActivationPhraseContent(tvm)
        TranscriptionContent(tvm)
        ErrorContent(tvm)
    }
}

@Composable
fun Controls(tvm: TranscriptionViewModel) {
    var buttonCapture by rememberSaveable { mutableStateOf("Start Capture") }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            modifier = Modifier.padding(10.dp),
            onClick = {
                if (buttonCapture == "Start Capture") {
                    tvm.reset()
                    SpeechRecognitionManager.start(tvm)
                    buttonCapture = "Stop Capture"
                } else {
                    SpeechRecognitionManager.stop()
                    buttonCapture = "Start Capture"
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
                tvm.reset()
            }
        ) {
            Text(
                text = "Reset Transcription"
            )
        }
    }
}

@Composable
fun HelpPhraseContent(tvm: TranscriptionViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = tvm.helpPhrase,
            onValueChange = { tvm.helpPhrase = it },
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
        if (tvm.helpPhraseTriggered) {
            Text(color = Color.Red, text = "Help Phrase Triggered")
        }
    }
}

@Composable
fun ActivationPhraseContent(tvm: TranscriptionViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = tvm.activationPhrase,
            onValueChange = { tvm.activationPhrase = it },
            label = { Text("Enter Activation Phrase") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TranscriptionContent(tvm: TranscriptionViewModel) {
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = tvm.messages)
    }
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(text = tvm.transcription)
    }
}

@Composable
fun ErrorContent(tvm: TranscriptionViewModel) {
    Column(
        modifier = Modifier
            .padding(vertical = 70.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = tvm.errors)
    }
}



@Preview(showBackground = true)
@Composable
fun SenderScreenPreview() {
    SenderScreen { }
}