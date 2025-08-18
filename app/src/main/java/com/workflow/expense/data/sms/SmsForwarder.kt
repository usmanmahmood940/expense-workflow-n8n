package com.workflow.expense.data.sms

import android.util.Log
import com.workflow.expense.BuildConfig
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.domain.usecase.ForwardLatestSmsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SmsForwarder(
    private val forwardLatestSmsUseCase: ForwardLatestSmsUseCase,
    private val prefsRepo: SharedPrefRepo
) {
    private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    @Volatile
    private var forwardingEnabled: Boolean = true

    sealed class ForwardEvent {
        data class Success(val from: String, val body: String) : ForwardEvent()
        data class Error(val from: String, val body: String, val error: String?) : ForwardEvent()
    }

    private val _events = MutableSharedFlow<ForwardEvent>()
    val events: SharedFlow<ForwardEvent> = _events

    fun setForwardingEnabled(enabled: Boolean) {
        forwardingEnabled = enabled
    }

    fun onIncomingSms(from: String, body: String) {
        if (!forwardingEnabled) return
        val expected = prefsRepo.bankNumber
        expected?.let {
            if (!from.contains(expected)) return
        }?:return

        val entity = SmsMessageEntity(
            message = body,
        )

        backgroundScope.launch {
            val result = forwardLatestSmsUseCase(entity)
            result.onSuccess {
                Log.i("SmsForwarder", "Forwarded SMS from $from")
                _events.emit(ForwardEvent.Success(from, it))
            }.onFailure {
                Log.e("SmsForwarder", "Failed to forward SMS", it)
                _events.emit(ForwardEvent.Error(from, body, it.message))
            }
        }
    }
}


