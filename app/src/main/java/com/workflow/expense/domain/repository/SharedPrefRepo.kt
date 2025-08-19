package com.workflow.expense.domain.repository

interface SharedPrefRepo {
    var bankNumber: List<String>?
    fun clearBankNumber()
    var baseUrl: String?
    var apiUrl: String?
}
