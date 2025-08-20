package com.workflow.expense.data.sms

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
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
    private val context: Context,
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

        val entity = SmsMessageEntity(message = body)
        if (isNetworkAvailable()) {
            backgroundScope.launch {
                when (val result = forwardLatestSmsUseCase(entity)) {
                    is ApiResult.Success<*> -> _events.emit(result)
                    ApiResult.NetworkError -> {
                        // Queue message for later retry
                        prefsRepo.addPendingMessage(body)
                        _events.emit(ApiResult.NetworkError)
                    }
                    is ApiResult.Error ->{
                        _events.emit(result)
                    }
                }
            }
        } else {
            // Queue message for later retry
            prefsRepo.addPendingMessage(body)
            backgroundScope.launch {
                _events.emit(ApiResult.NetworkError)
            }
        }
    }

    fun flushPendingIfAny() {
        backgroundScope.launch {
            val pending = prefsRepo.getPendingMessages().toMutableList()
            if (pending.isEmpty()) return@launch
            val iterator = pending.iterator()
            while (iterator.hasNext()) {
                val body = iterator.next()
                val entity = SmsMessageEntity(message = body)
                if (isNetworkAvailable()) {
                    when (val result = forwardLatestSmsUseCase(entity)) {
                        is ApiResult.Success<*> -> {
                            iterator.remove()
                            prefsRepo.setPendingMessages(pending)
                            _events.emit(result)
                        }
                        ApiResult.NetworkError -> {
                            // Stop trying further; will retry next connectivity event
                            _events.emit(ApiResult.NetworkError)
                            break
                        }
                        is ApiResult.Error -> {
                            // Skip and remove on 4xx/5xx? For safety, keep it and break to retry later
                            _events.emit(result)
                            break
                        }
                    }
                } else {
                    // Stop trying further; will retry next connectivity event
                    _events.emit(ApiResult.NetworkError)
                    break
                }
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}