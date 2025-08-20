package com.workflow.expense.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.workflow.expense.presentation.viewmodel.SettingsViewModel
import com.workflow.expense.presentation.mvi.SettingsEffect
import com.workflow.expense.presentation.mvi.SettingsIntent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.SavedSuccessfully -> { /* Could show a snackbar */ }
                is SettingsEffect.ShowError -> { /* Could show a snackbar with effect.message */ }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.baseUrlInput,
            onValueChange = { viewModel.dispatch(SettingsIntent.UpdateBaseUrlInput(it)) },
            label = { Text("Workflow Url") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            isError = state.errorMessage != null
        )
        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(state.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = { viewModel.dispatch(SettingsIntent.SaveBaseUrl) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (state.saveSuccess) "Saved" else "Save")
            }
        }
    }
}
