package com.workflow.expense.domain.repository

interface SharedPrefRepo {
    var bankNumber: List<String>?
    fun clearBankNumber()
    var baseUrl: String?

    // Pending SMS queue for offline mode
    fun getPendingMessages(): List<String>
    fun setPendingMessages(messages: List<String>)
    fun addPendingMessage(messageBody: String)
}
