package com.dolphinbaytech.dolomessenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ActivationScreen(
    onStateChange: (String) -> Unit
) {
    // Create variable that updates UI, since DoLoServerAPI is not a View Model
    var server by rememberSaveable { mutableStateOf(DoLoServerAPI.server) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to DoLo Messenger")
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                onStateChange("Sender")
            }
        ) {
            Text(text = "Activate as Sender")
        }
        Row(modifier = Modifier.padding(10.dp)) {}
        Button(
            onClick = {
                onStateChange("Receiver")
            }
        ) {
            Text(text = "Activate as Receiver")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        TextField(
            value = server,
            onValueChange = {
                server = it
                DoLoServerAPI.updateServer(it)
            },
            label = { Text("Server URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ActivationScreenPreview() {
    ActivationScreen {  }
}

