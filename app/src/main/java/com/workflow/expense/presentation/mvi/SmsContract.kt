package com.workflow.expense.presentation.mvi

data class SmsState(
    val latestMessage: String = "",
    val lastFrom: String = "",
    val isForwardingEnabled: Boolean = true,
    val apiStatus: ApiStatus = ApiStatus.Idle,
)

sealed class ApiStatus {
    data object Idle : ApiStatus()
    data object Loading : ApiStatus()
    data class Success(val atMillis: Long) : ApiStatus()
    data class Error(val message: String?) : ApiStatus()
}

sealed interface SmsIntent {
    data class ToggleForwarding(val enabled: Boolean) : SmsIntent
    data object RequestPermissions : SmsIntent
    data class SendManualSms(val from: String, val message: String) : SmsIntent
    data class SetBankNumber(val number: String) : SmsIntent

}


