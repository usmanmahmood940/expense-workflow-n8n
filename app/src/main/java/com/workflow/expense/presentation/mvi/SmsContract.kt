package com.workflow.expense.presentation.mvi


sealed class ApiStatus {
    data object Idle : ApiStatus()
    data object Loading : ApiStatus()
    data class Success(val atMillis: Long) : ApiStatus()
    data class Error(val message: String?) : ApiStatus()
}

data class SmsState(
    val isForwardingEnabled: Boolean = false,
    val latestMessage: String = "",
    val lastFrom: String = "",
    val apiStatus: ApiStatus = ApiStatus.Idle,
    val savedBankNumbers: List<String> = emptyList() // Add this line
)

// Add these to your SmsIntent sealed class
sealed class SmsIntent {
    data class ToggleForwarding(val enabled: Boolean) : SmsIntent()
    data class SendLogExpense(val from: String, val message: String) : SmsIntent()
    data class SetBankNumbers(val numbers: List<String>) : SmsIntent() // Updated from SetBankNumber
    object LoadSavedBankNumbers : SmsIntent() // Add this line
}


