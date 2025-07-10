package com.example.dolomessenger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dolomessenger.ui.theme.DoLoMessengerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissions()
        SpeechRecognitionManager.create(applicationContext)
        DoLoServerAPI.create(applicationContext)

        setContent {
            DoLoMessengerTheme {
                HomeScreen()
            }
        }
    }

    private fun requestPermissions() {
        var permissions = emptyArray<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions += Manifest.permission.POST_NOTIFICATIONS
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions += Manifest.permission.RECORD_AUDIO
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
        // TODO - handle the scenario where permissions are not approved
    }
}

@Composable
fun HomeScreen() {
    var state by rememberSaveable { mutableStateOf("Activation") }

    when (state) {
        "Activation" -> { ActivationScreen { state = it } }
        "Sender" -> SenderScreen {state = it}
        "Receiver" -> ReceiverScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun DoLoMessengerPreview() {
    DoLoMessengerTheme {
        HomeScreen()
    }
}