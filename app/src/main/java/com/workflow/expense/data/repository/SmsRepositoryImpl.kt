package com.workflow.expense.data.repository

import com.workflow.expense.data.remote.ApiService
import com.workflow.expense.data.utils.safeApiCall
import com.workflow.expense.domain.model.ApiResult
import com.workflow.expense.domain.model.ForwardSmsRequest
import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.model.TransactionDetail
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.domain.repository.SmsRepository
import okhttp3.Response

class SmsRepositoryImpl(
    private val api: ApiService,
    private val sharedPref: SharedPrefRepo
) : SmsRepository {
    override suspend fun forwardSms(message: SmsMessageEntity): ApiResult<List<TransactionDetail>> =
        safeApiCall { api.forwardSms("", ForwardSmsRequest(message.message)) }

}


