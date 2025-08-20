package com.workflow.expense.data.di

import com.workflow.expense.data.repository.SmsRepositoryImpl
import com.workflow.expense.data.sms.SmsForwarder
import com.workflow.expense.domain.repository.SmsRepository
import com.workflow.expense.domain.usecase.ForwardLatestSmsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.dsl.module

val repositoryModule = module {
    single<SmsRepository> { SmsRepositoryImpl(get(),get()) }
    single { SmsForwarder(get(),get(),get()) }
}

val useCaseModule = module {
    single { ForwardLatestSmsUseCase(get(), get<CoroutineDispatcher>()) }
}


