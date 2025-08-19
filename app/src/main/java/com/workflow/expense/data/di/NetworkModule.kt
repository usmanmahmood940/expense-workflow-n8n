package com.workflow.expense.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.workflow.expense.BuildConfig
import com.workflow.expense.data.remote.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single<Gson> { GsonBuilder().create() }
    single {
        val logging: Interceptor = HttpLoggingInterceptor().apply {
            (this as HttpLoggingInterceptor).level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(60, TimeUnit.SECONDS) // Increase connection timeout
            .readTimeout(60, TimeUnit.SECONDS)    // Increase read timeout
            .writeTimeout(60, TimeUnit.SECONDS)   // Increase write timeout
            .build()
    }
    single {
        val sharedPrefRepo = get<com.workflow.expense.domain.repository.SharedPrefRepo>()
        Retrofit.Builder()
            .baseUrl(sharedPrefRepo.baseUrl ?: BuildConfig.API_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single<ApiService> { get<Retrofit>().create(ApiService::class.java) }
}


