package com.workflow.expense.domain.repository

import com.workflow.expense.data.remote.ForwardSmsRequest
import com.workflow.expense.domain.model.SmsMessageEntity
import okhttp3.Response

interface SmsRepository {
    suspend fun forwardSms(message: SmsMessageEntity): Result<String>
}


