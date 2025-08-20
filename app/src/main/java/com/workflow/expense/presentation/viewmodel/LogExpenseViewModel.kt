package com.workflow.expense.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.workflow.expense.domain.model.ApiResult
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.usecase.ForwardLatestSmsUseCase
import com.workflow.expense.presentation.mvi.BaseMviViewModel
import com.workflow.expense.presentation.mvi.LogExpenseEffect
import com.workflow.expense.presentation.mvi.LogExpenseIntent
import com.workflow.expense.presentation.mvi.LogExpenseState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogExpenseViewModel(
        private val forwardLatestSmsUseCase: ForwardLatestSmsUseCase,
) : BaseMviViewModel<LogExpenseIntent, LogExpenseState, LogExpenseEffect>(LogExpenseState()) {

    override fun dispatch(intent: LogExpenseIntent) {
        viewModelScope.launch {
            when (intent) {
                is LogExpenseIntent.UpdateMessageBody -> {
                    _state.update { it.copy(messageBody = intent.body, errorMessage = null) }
                }

                is LogExpenseIntent.SendMessage -> performSendMessage()
                is LogExpenseIntent.ResetStatus -> _state.update { it.copy(sendSuccess = false, errorMessage = null) }
            }
        }
    }

    private fun performSendMessage() {
        val currentMessage = state.value.messageBody.trim()
        if (currentMessage.isBlank()) {
            _state.update { it.copy(errorMessage = "Message body cannot be empty.") }
            viewModelScope.launch { _effect.emit(LogExpenseEffect.ShowError("Message body cannot be empty.")) }
            return
        }

        _state.update { it.copy(isSending = true, sendSuccess = false, errorMessage = null) }

        viewModelScope.launch {
            val result = forwardLatestSmsUseCase(SmsMessageEntity(currentMessage))
            when (result) {
                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                                isSending = false,
                                sendSuccess = false,
                                errorMessage = result.message
                        )
                    }
                    _effect.emit(LogExpenseEffect.ShowError(result.message ?: "Unknown error"))
                }

                ApiResult.NetworkError -> {
                    _state.update {
                        it.copy(
                                isSending = false,
                                sendSuccess = false,
                                errorMessage = "No Internet"
                        )
                    }
                    _effect.emit(LogExpenseEffect.ShowError("No Internet"))
                }

                is ApiResult.Success<*> -> {
                    _state.update { state ->
                        state.copy(
                                isSending = false,
                                sendSuccess = true,
                                messageBody = "",
                                apiResponse = result.data.toString()
                        )
                    }
                    _effect.emit(LogExpenseEffect.MessageSent)
                }
            }
        }
    }
}


