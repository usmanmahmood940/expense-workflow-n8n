package com.workflow.expense.data.repository

import com.workflow.expense.data.remote.ApiService
import com.workflow.expense.data.remote.ForwardSmsRequest
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.repository.SmsRepository
import okhttp3.Response

class SmsRepositoryImpl(
    private val api: ApiService,
) : SmsRepository {
    override suspend fun forwardSms(message: SmsMessageEntity): Result<String> = try {
        val response = api.forwardSms(ForwardSmsRequest(message = message.message))
        if (response.isSuccessful) Result.success(response.body()?.message?:message.message) else Result.failure(IllegalStateException("HTTP ${'$'}{response.code()}"))
    } catch (t: Throwable) {
        Result.failure(t)
    }
}


