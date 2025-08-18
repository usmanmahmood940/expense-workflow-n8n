package com.workflow.expense.domain.usecase

import com.workflow.expense.domain.model.SmsMessageEntity
import com.workflow.expense.domain.repository.SmsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ForwardLatestSmsUseCase(
    private val repository: SmsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(message: SmsMessageEntity): Result<String> = withContext(ioDispatcher) {
        repository.forwardSms(message)
    }
}


