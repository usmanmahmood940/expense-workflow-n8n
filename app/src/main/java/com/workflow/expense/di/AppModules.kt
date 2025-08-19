package com.workflow.expense.di

import com.workflow.expense.data.di.dispatcherModule
import com.workflow.expense.data.di.networkModule
import com.workflow.expense.data.di.repositoryModule
import com.workflow.expense.data.di.sharedPrefModule
import com.workflow.expense.data.di.useCaseModule
import com.workflow.expense.presentation.LogExpenseViewModel
import com.workflow.expense.presentation.SettingsViewModel
import com.workflow.expense.presentation.SmsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SmsViewModel(get(),get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { LogExpenseViewModel(get()) }

}

val appModules = listOf(
    networkModule,
    dispatcherModule,
    repositoryModule,
    useCaseModule,
    viewModelModule,
    sharedPrefModule,
)


