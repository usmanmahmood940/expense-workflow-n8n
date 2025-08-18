package com.workflow.expense.data.repository

import android.content.SharedPreferences
import com.workflow.expense.domain.repository.SharedPrefRepo
import androidx.core.content.edit

class SharedPrefRepoImpl(private val sharedPreferences: SharedPreferences) : SharedPrefRepo {

    companion object {
        private const val KEY_BANK_NUMBER = "bank_number"
    }

    override var bankNumber: String?
        get() = sharedPreferences.getString(KEY_BANK_NUMBER, null)
        set(value) {
            sharedPreferences.edit { putString(KEY_BANK_NUMBER, value) }
        }

    override fun clearBankNumber() {
        sharedPreferences.edit { remove(KEY_BANK_NUMBER) }
    }
}
