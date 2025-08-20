package com.workflow.expense.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.workflow.expense.data.sms.SmsForwarder
import com.workflow.expense.domain.model.ApiResult
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.presentation.mvi.ApiStatus
import com.workflow.expense.presentation.mvi.ApiStatus.*
import com.workflow.expense.presentation.mvi.BaseMviViewModel
import com.workflow.expense.presentation.mvi.SmsEffect
import com.workflow.expense.presentation.mvi.SmsIntent
import com.workflow.expense.presentation.mvi.SmsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SmsViewModel(
    private val forwarder: SmsForwarder,
    private val sharedPrefRepo: SharedPrefRepo,
) : BaseMviViewModel<SmsIntent, SmsState, SmsEffect>(SmsState()) {

    init {
        viewModelScope.launch {
            forwarder.events.collectLatest { event ->
                when (event) {
                    is ApiResult.Error -> _state.update {
                        it.copy(
                            latestMessage = event.message,
                            apiStatus = Error(event.message),
                        )
                    }
                    ApiResult.NetworkError -> _state.update {
                        it.copy(
                            latestMessage = "No internet",
                            apiStatus = Error("No internet"),
                        )
                    }
                    is ApiResult.Success<*> -> _state.update {
                        it.copy(
                            latestMessage = event.data.toString(),
                            apiStatus = Success(System.currentTimeMillis()),
                        )
                    }
                }
            }
        }
    }

    override fun dispatch(intent: SmsIntent) {
        when (intent) {
            is SmsIntent.ToggleForwarding -> toggleForwarding(intent.enabled)
            is SmsIntent.SendLogExpense -> forwarder.onIncomingSms(intent.from, intent.message)
            is SmsIntent.SetBankNumbers -> saveBankNumbers(intent.numbers)
            SmsIntent.LoadSavedBankNumbers -> loadSavedBankNumbers()
        }
    }

    private fun toggleForwarding(enabled: Boolean) {
        viewModelScope.launch {
            forwarder.setForwardingEnabled(enabled)
            _state.update { it.copy(isForwardingEnabled = enabled, apiStatus = ApiStatus.Idle) }
        }
    }

    private fun saveBankNumbers(numbers: List<String>) {
        sharedPrefRepo.bankNumber = numbers
        _state.update { it.copy(savedBankNumbers = numbers) }
        viewModelScope.launch { _effect.emit(SmsEffect.SavedBankNumbers) }
    }

    private fun loadSavedBankNumbers() {
        val savedNumbers = sharedPrefRepo.bankNumber ?: emptyList()
        _state.update { it.copy(savedBankNumbers = savedNumbers) }
    }
}


