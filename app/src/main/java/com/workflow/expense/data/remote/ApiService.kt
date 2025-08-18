package com.workflow.expense.data.remote

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ForwardSmsRequest(
    val message: String,
)

interface ApiService {
    @POST("e0a3a07e-70ff-4901-805a-8c01315fec91") // using httpbin.org/post by default; override base URL in BuildConfig
    suspend fun forwardSms(@Body body: ForwardSmsRequest): Response<ForwardSmsRequest>
}


