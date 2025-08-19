package com.workflow.expense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.usecase.ForwardLatestSmsUseCase
import com.workflow.expense.presentation.mvi.LogExpenseIntent
import com.workflow.expense.presentation.mvi.LogExpenseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogExpenseViewModel(
    private val forwardLatestSmsUseCase: ForwardLatestSmsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogExpenseState())
    val uiState: StateFlow<LogExpenseState> = _uiState.asStateFlow()

    fun processIntent(intent: LogExpenseIntent) {
        viewModelScope.launch {
            when (intent) {
                is LogExpenseIntent.UpdateMessageBody -> {
                    _uiState.update { it.copy(messageBody = intent.body, errorMessage = null) }
                }

                is LogExpenseIntent.SendMessage -> {
                    performSendMessage()
                }

                is LogExpenseIntent.ResetStatus -> {
                    _uiState.update { it.copy(sendSuccess = false, errorMessage = null) }
                }
            }
        }
    }

    private fun performSendMessage() {
        val currentMessage = _uiState.value.messageBody.trim()
        if (currentMessage.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Message body cannot be empty.") }
            return
        }

        _uiState.update { it.copy(isSending = true, sendSuccess = false, errorMessage = null) }

        viewModelScope.launch {
            val result = forwardLatestSmsUseCase(SmsMessageEntity(currentMessage))
            result.isSuccess
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(
                        isSending = false,
                        sendSuccess = true,
                        messageBody = it  // Clear fields on success
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        sendSuccess = false,
                        errorMessage = exception.message ?: "Failed to send message."
                    )
                }
            }
        }
    }
}
 

