package com.workflow.expense.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.workflow.expense.presentation.viewmodel.LogExpenseViewModel
import com.workflow.expense.presentation.mvi.LogExpenseEffect
import com.workflow.expense.presentation.mvi.LogExpenseIntent
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogExpenseScreen(
    viewModel: LogExpenseViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val context = LocalContext.current // For Toasts or other context needs
    val message = uiState.messageBody

    // Effect to show a message when sendSuccess changes or an error occurs
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LogExpenseEffect.MessageSent -> Toast.makeText(context, "Message Sent!", Toast.LENGTH_SHORT).show()
                is LogExpenseEffect.ShowError -> Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log Expense",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                // Handle window insets for status bar if TopAppBar content needs it
                // modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from Scaffold
                .padding(16.dp) // Content padding
                .verticalScroll(rememberScrollState()) // Make content scrollable if it overflows
                // Pad bottom for navigation bar if content might go behind it
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.messageBody,
                onValueChange = { viewModel.dispatch(LogExpenseIntent.UpdateMessageBody(it)) },
                label = { Text("Type Bank Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp), // Make message field taller
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done // Or ImeAction.Send if you want keyboard send
                ),
                isError = uiState.errorMessage?.contains("Message", ignoreCase = true) == true
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { viewModel.dispatch(LogExpenseIntent.SendMessage) },
                enabled = !uiState.isSending,
                modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
            ) {
                Row(modifier = Modifier.padding(10.dp)) {
                    if (uiState.isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Send Message")
                    }
                }
            }

//            if (uiState.sendSuccess)
//            uiState.apiResponse?.let {
//                Text(it, modifier = Modifier.padding(10.dp))
//            }
        }
    }
}
 

