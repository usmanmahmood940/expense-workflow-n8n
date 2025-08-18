package com.workflow.expense.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workflow.expense.data.sms.SmsForwarder
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.presentation.mvi.ApiStatus
import com.workflow.expense.presentation.mvi.SmsIntent
import com.workflow.expense.presentation.mvi.SmsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SmsViewModel(
    private val forwarder: SmsForwarder,
    private val sharedPrefRepo: SharedPrefRepo,
) : ViewModel() {

    private val _state = MutableStateFlow(SmsState(lastFrom = sharedPrefRepo.bankNumber?:""))
    val state: StateFlow<SmsState> = _state

    init {
        viewModelScope.launch {
            forwarder.events.collectLatest { event ->
                when (event) {
                    is SmsForwarder.ForwardEvent.Success -> _state.update {
                        it.copy(
                            latestMessage = event.body,
                            apiStatus = ApiStatus.Success(System.currentTimeMillis()),
                        )
                    }
                    is SmsForwarder.ForwardEvent.Error -> _state.update {
                        it.copy(
                            latestMessage = event.body,
                            apiStatus = ApiStatus.Error(event.error),
                        )
                    }
                }
            }
        }
    }

    fun dispatch(intent: SmsIntent) {
        when (intent) {
            is SmsIntent.ToggleForwarding -> toggleForwarding(intent.enabled)
            SmsIntent.RequestPermissions -> {
                // no-op here; the UI will handle requesting
            }

            is SmsIntent.SendManualSms -> forwarder.onIncomingSms(intent.from,intent.message)
            is SmsIntent.SetBankNumber -> {
                sharedPrefRepo.bankNumber=intent.number
            }
        }
    }

    private fun toggleForwarding(enabled: Boolean) {
        viewModelScope.launch {
            forwarder.setForwardingEnabled(enabled)
            _state.update { it.copy(isForwardingEnabled = enabled, apiStatus = ApiStatus.Idle) }
        }
    }
}


