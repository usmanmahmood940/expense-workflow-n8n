package com.workflow.expense.data.sms

import android.util.Log
import com.workflow.expense.BuildConfig
import com.workflow.expense.domain.model.ApiResult
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.model.TransactionDetail
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


    private val _events = MutableSharedFlow<ApiResult<List<TransactionDetail>>>()
    val events: SharedFlow<ApiResult<List<TransactionDetail>>> = _events

    fun setForwardingEnabled(enabled: Boolean) {
        forwardingEnabled = enabled
    }

    fun onIncomingSms(from: String, body: String) {
        if (!forwardingEnabled) return
        val expectedNumbers = prefsRepo.bankNumber
        if (expectedNumbers.isNullOrEmpty()) return

        val isFromExpectedNumber = expectedNumbers.any { expected ->
            from.equals(expected, ignoreCase = true)
        }
        if (!isFromExpectedNumber) return

        val entity = SmsMessageEntity(
            message = body,
        )

        backgroundScope.launch {
            val result = forwardLatestSmsUseCase(entity)
            _events.emit(result)
        }
    }
}


