package com.dolphinbaytech.dolomessenger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ReceiverScreen(
    rvm: ReceiverViewModel,
    onStateChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(text = "Notifications")
        }
        Row(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(text = rvm.notifications)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = { onStateChange("Activation") }
        ) {
            Text(text = "Switch App Mode")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReceiverScreenPreview() {
    val rvm: ReceiverViewModel = viewModel()
    ReceiverScreen(rvm) { }
}