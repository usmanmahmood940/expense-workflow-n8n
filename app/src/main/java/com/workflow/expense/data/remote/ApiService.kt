package com.workflow.expense.data.remote

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import retrofit2.Response

data class ForwardSmsRequest(
    val message: String,
)

interface ApiService {
    @POST
    suspend fun forwardSms(@Url url: String, @Body body: ForwardSmsRequest): Response<ForwardSmsRequest>
}
