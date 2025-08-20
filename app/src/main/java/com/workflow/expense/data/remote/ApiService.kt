package com.workflow.expense.data.remote

import com.workflow.expense.domain.model.ForwardSmsRequest
import com.workflow.expense.domain.model.TransactionDetail
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import retrofit2.Response


interface ApiService {
    @POST("")
    suspend fun forwardSms( @Url url: String,@Body body: ForwardSmsRequest): Response<List<TransactionDetail>>
}