package com.workflow.expense.data.di

import android.content.Context
import android.content.SharedPreferences
import com.workflow.expense.data.repository.SharedPrefRepoImpl
import com.workflow.expense.domain.repository.SharedPrefRepo
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPrefModule = module {

    // Provide SharedPreferences instance
    single<SharedPreferences> {
        androidContext().getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)
    }

    // Provide SharedPreferenceHelper instance
    single<SharedPrefRepo> {
        SharedPrefRepoImpl(get()) // Koin will resolve and inject SharedPreferences here
    }
}