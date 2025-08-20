package com.workflow.expense.domain.repository

import com.workflow.expense.domain.model.ApiResult
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.model.TransactionDetail
import okhttp3.Response

interface SmsRepository {
    suspend fun forwardSms(message: SmsMessageEntity): ApiResult<List<TransactionDetail>>
}


