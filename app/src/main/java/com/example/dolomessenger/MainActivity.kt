package com.example.dolomessenger

import android.Manifest
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dolomessenger.ui.theme.DoLoMessengerTheme
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel

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
    val sharedPref = LocalContext.current.getSharedPreferences("DoLo Messenger", Context.MODE_PRIVATE)
    val appModeSaved = sharedPref.getString("App Mode", "Activation") ?: "Activation"

    var appMode by rememberSaveable { mutableStateOf(appModeSaved) }

    when (appMode) {
        "Activation" -> {
            ActivationScreen {
                appMode = it
                sharedPref.edit {
                    putString("App Mode", appMode)
                    apply()
                }
            }
        }
        "Sender" -> {
            val svm: SenderViewModel = viewModel()
            svm.create(LocalContext.current)
            SenderScreen(svm) {
                appMode = it
                sharedPref.edit {
                    putString("App Mode", "Activation")
                    apply()
                }
            }
        }
        "Receiver" -> {
            val rvm: ReceiverViewModel = viewModel()
            ReceiverScreen(rvm) {
                appMode = it
                sharedPref.edit {
                    putString("App Mode", "Activation")
                    apply()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoLoMessengerPreview() {
    DoLoMessengerTheme {
        HomeScreen()
    }
}