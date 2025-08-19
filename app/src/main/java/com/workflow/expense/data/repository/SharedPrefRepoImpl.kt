package com.workflow.expense.data.repository

import android.content.SharedPreferences
import com.workflow.expense.domain.repository.SharedPrefRepo
import androidx.core.content.edit

class SharedPrefRepoImpl(private val sharedPreferences: SharedPreferences) : SharedPrefRepo {

    companion object {
        private const val KEY_BANK_NUMBER = "bank_number"
        private const val KEY_BASE_URL = "base_url"
        private const val DEFAULT_API_URL = "e0a3a07e-70ff-4901-805a-8c01315fec91"
    }

    override var bankNumber: List<String>?
        get() {
            val json = sharedPreferences.getString(KEY_BANK_NUMBER, null)
            return json?.split(",")?.toList()
        }
        set(value) {
            val json = value?.joinToString(",")
            sharedPreferences.edit { putString(KEY_BANK_NUMBER, json) }
        }


    override var baseUrl: String?
        get() = sharedPreferences.getString(
            KEY_BASE_URL,
            com.workflow.expense.BuildConfig.API_BASE_URL
        )
        set(value) {
            sharedPreferences.edit { putString(KEY_BASE_URL, value) }
        }

//    override var apiUrl: String?
//        get() = sharedPreferences.getString(KEY_API_URL, DEFAULT_API_URL)
//        set(value) {
//            sharedPreferences.edit { putString(KEY_API_URL, value) }
//        }

    override fun clearBankNumber() {
        sharedPreferences.edit { remove(KEY_BANK_NUMBER) }
    }
}
