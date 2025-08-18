package com.workflow.expense.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.workflow.expense.presentation.SmsViewModel
import com.workflow.expense.presentation.mvi.ApiStatus
import com.workflow.expense.presentation.mvi.SmsIntent

@Composable
fun SmsScreen(viewModel: SmsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Request permissions at launch
        val missing = REQUIRED_PERMISSIONS.filter {
            ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty() && context is android.app.Activity) {
            ActivityCompat.requestPermissions(context, missing.toTypedArray(), 101)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Text("Forwarding", style = MaterialTheme.typography.titleMedium)
//            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
//            Switch(checked = state.isForwardingEnabled, onCheckedChange = {
//                viewModel.dispatch(SmsIntent.ToggleForwarding(it))
//            })
//        }

//        Spacer(modifier = Modifier.height(16.dp))
//        Text("Latest forwarded message:")
//        Text("From: ${state.lastFrom}")

//        Spacer(modifier = Modifier.height(16.dp))
//        Text("API status:")
//        when (val s = state.apiStatus) {
//            ApiStatus.Idle -> Text("Idle")
//            ApiStatus.Loading -> Text("Sending...")
//            is ApiStatus.Success -> Text("Success @ ${state.apiStatus}")
//            is ApiStatus.Error -> Text("Error: ${s.message ?: "unknown"}")
//        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                val missing = REQUIRED_PERMISSIONS.filter {
                    ActivityCompat.checkSelfPermission(
                        context,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                }
                if (missing.isNotEmpty() && context is android.app.Activity) {
                    ActivityCompat.requestPermissions(context, missing.toTypedArray(), 102)
                }
            }) {
                Text("Request Permissions")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val senderNumber = remember { mutableStateOf(state.lastFrom) }
        val messageText = remember { mutableStateOf("PKR 2,000.00 received from AHSAN JAVED (MCB ACxxx5510) to your A/C xxx4521 on 02-Jun-2025 at 10:11 TID:884521") }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
            Text("Bank Number:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = senderNumber.value,
                onValueChange = { senderNumber.value = it },
                label = { Text("Sender Number") },
                modifier = Modifier
                    .padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
//        TextField(
//            value = messageText.value,
//            onValueChange = { messageText.value = it },
//            label = { Text("Message") },
//            modifier = Modifier
//                .padding(vertical = 4.dp)
//        )
            Button(enabled = if (senderNumber.value.isNotEmpty()) true else false, onClick = {
                viewModel.dispatch(
                    SmsIntent.SetBankNumber(
                        number = senderNumber.value,
                    )
                )
            }) {
                Text("Set Bank Numner")
            }
            Text(state.latestMessage)

        }

//        Spacer(modifier = Modifier.height(8.dp))
//        Button(onClick = {
//            viewModel.dispatch(
//                SmsIntent.SendManualSms(
//                    from = senderNumber.value,
//                    message = messageText.value
//                )
//            )
//        }) {
//            Text("Send Manually")
//        }
    }
}

private val REQUIRED_PERMISSIONS = listOf(
    Manifest.permission.RECEIVE_SMS,
    Manifest.permission.READ_SMS,
)


