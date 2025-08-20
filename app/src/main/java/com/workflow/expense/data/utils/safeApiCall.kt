package com.workflow.expense.data.utils

import com.workflow.expense.domain.model.ApiResult
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = apiCall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Empty response body", response.code())
            }
        } else {
            val errorMsg = response.errorBody()?.string() ?: "Unknown error"
            ApiResult.Error(errorMsg, response.code())
        }
    } catch (e: IOException) {
        ApiResult.NetworkError
    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: "Unexpected error")
    }
}
