package com.workflow.expense.ui

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.workflow.expense.R
import com.workflow.expense.presentation.viewmodel.SmsViewModel
import com.workflow.expense.presentation.mvi.SmsEffect
import com.workflow.expense.presentation.mvi.SmsIntent
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SmsScreen(
    viewModel: SmsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // State for managing bank numbers - initialize with saved numbers or empty field
    var bankNumbers by remember { mutableStateOf(listOf("")) }

    // Load saved bank numbers on initialization
    LaunchedEffect(Unit) { viewModel.dispatch(SmsIntent.LoadSavedBankNumbers) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SmsEffect.SavedBankNumbers -> { /* could show snackbar */ }
                is SmsEffect.ShowError -> { /* hook into a snackbar host if desired */ }
            }
        }
    }

    // Update local state when saved numbers are loaded
    LaunchedEffect(state.savedBankNumbers) {
        if (state.savedBankNumbers.isNotEmpty()) {
            bankNumbers = state.savedBankNumbers.toList()
        }
    }

    // Permission state
    @OptIn(ExperimentalPermissionsApi::class)
    val permissionsState = rememberMultiplePermissionsState(permissions = REQUIRED_PERMISSIONS)

    // Check permissions status
    val hasPermissions by remember(permissionsState) {
        derivedStateOf { permissionsState.allPermissionsGranted }
    }

    // Request permissions if not granted
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    // Custom color scheme
    val gradientColors = listOf(
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.05f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.sms_expense_tracker),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            stringResource(R.string.turn_bank_alerts),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding(vertical = 4.dp),
            )
        },
        content = { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = gradientColors
                        )
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            )
            {
                // Enhanced Top Bar


                Spacer(modifier = Modifier.height(10.dp))

                // Enhanced Permission Status Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    border = if (hasPermissions) {
                        BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                    } else {
                        BorderStroke(
                            2.dp,
                            Color(0xFFFF5722).copy(alpha = 0.3f)
                        )
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.background(Color.White)){
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                if (hasPermissions) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (hasPermissions) MaterialTheme.colorScheme.primary else Color(0xFFFF5722),
                                modifier = Modifier.size(28.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (hasPermissions) stringResource(R.string.permissions_granted) else stringResource(
                                        R.string.permissions_required
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hasPermissions) MaterialTheme.colorScheme.primary else Color(0xFFD84315)
                                )
                                Text(
                                    text = if (hasPermissions) stringResource(R.string.sms_access_enabled) else stringResource(
                                        R.string.enable_sms_access
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!hasPermissions) {
                                Button(
                                    onClick = {
                                        permissionsState.launchMultiplePermissionRequest()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF5722),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        stringResource(R.string.grant),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Enhanced Bank Numbers Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Header with gradient background
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.bank_numbers),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (state.savedBankNumbers.isNotEmpty()) {
                                    Text(
                                        text = stringResource(
                                            R.string.numbers_saved,
                                            state.savedBankNumbers.size
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.no_numbers_saved_yet),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "${bankNumbers.size}/4",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced Bank Number Text Fields
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .heightIn(max = 320.dp)
                        ) {
                            itemsIndexed(bankNumbers) { index, number ->
                                EnhancedBankNumberField(
                                    value = number,
                                    onValueChange = { newValue ->
                                        bankNumbers = bankNumbers.toMutableList().apply {
                                            this[index] = newValue
                                        }
                                    },
                                    onDelete = if (bankNumbers.size > 1 && index > 0) {
                                        {
                                            bankNumbers = bankNumbers.toMutableList().apply {
                                                removeAt(index)
                                            }
                                        }
                                    } else null,
                                    index = index
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                            }

                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Enhanced Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (bankNumbers.size < 4) {
                                        bankNumbers = bankNumbers + ""
                                    }
                                },
                                enabled = bankNumbers.size < 4,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                border = BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.add), fontWeight = FontWeight.SemiBold)

                            }

                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Enhanced Latest Message Section
                if (state.latestMessage.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF673AB7).copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFF673AB7).copy(alpha = 0.2f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF673AB7).copy(alpha = 0.2f)
                                ) {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Color(0xFF673AB7),
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(2.dp)
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.latest_message),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4527A0)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.latestMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Enhanced Main Action Button
                Button(
                    onClick = {
                        val validNumbers = bankNumbers.filter { it.isNotBlank() }
                        if (validNumbers.isNotEmpty()) {
                            viewModel.dispatch(
                                SmsIntent.SetBankNumbers(
                                    numbers = validNumbers
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    ),
                    enabled = bankNumbers.any { it.isNotBlank() } &&
                            bankNumbers.filter { it.isNotBlank() } != state.savedBankNumbers,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        if (state.savedBankNumbers.isEmpty()) stringResource(R.string.save_numbers) else stringResource(R.string.update_numbers),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
    )


}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun EnhancedBankNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    onDelete: (() -> Unit)?,
    index: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Number indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "${index + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        stringResource(R.string.bank_number),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                placeholder = {
                    Text(
                        stringResource(R.string.enter_bank_number),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
            )

            if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFFF5722).copy(alpha = 0.1f),
                        contentColor = Color(0xFFD84315)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private val REQUIRED_PERMISSIONS = listOf(
    Manifest.permission.RECEIVE_SMS,
    Manifest.permission.READ_SMS,
)