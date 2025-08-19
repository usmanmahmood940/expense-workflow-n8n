package com.workflow.expense.presentation.mvi

data class LogExpenseState(
    val messageBody: String = "",
    val isSending: Boolean = false,
    val sendSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed class LogExpenseIntent {
    data class UpdateMessageBody(val body: String) : LogExpenseIntent()
    object SendMessage : LogExpenseIntent()
    object ResetStatus : LogExpenseIntent() // To reset sendSuccess and errorMessage
}


