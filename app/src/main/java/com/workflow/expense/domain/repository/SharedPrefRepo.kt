package com.workflow.expense.domain.repository

interface SharedPrefRepo {
    var bankNumber: String?
    fun clearBankNumber()
}
