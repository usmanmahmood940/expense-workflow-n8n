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
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.presentation.mvi.LogExpenseIntent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.LocalKoinScope
import org.koin.compose.koinInject

@Composable
fun SettingsScreen() {
    val sharedPrefRepo = koinInject<SharedPrefRepo>()
    var baseUrl by remember { mutableStateOf(sharedPrefRepo.baseUrl ?: "") }
    var apiUrl by remember { mutableStateOf(sharedPrefRepo.apiUrl ?: "") }

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
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp), // Make message field taller
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done // Or ImeAction.Send if you want keyboard send
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = apiUrl,
            onValueChange = { apiUrl = it },
            label = { Text("API URL") },
            modifier = Modifier
                .fillMaxWidth(),// Make message field taller
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done // Or ImeAction.Send if you want keyboard send
            ),
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = {
            sharedPrefRepo.baseUrl = baseUrl
            sharedPrefRepo.apiUrl = apiUrl
        }, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(10.dp)) {
                Text("Save")
            }
        }

    }
}
