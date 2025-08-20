package com.workflow.expense.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.presentation.mvi.BaseMviViewModel
import com.workflow.expense.presentation.mvi.SettingsEffect
import com.workflow.expense.presentation.mvi.SettingsIntent
import com.workflow.expense.presentation.mvi.SettingsState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL

class SettingsViewModel(private val prefsRepo: SharedPrefRepo) :
    BaseMviViewModel<SettingsIntent, SettingsState, SettingsEffect>(SettingsState()) {

    init {
        dispatch(SettingsIntent.LoadInitialSettings)
    }

    override fun dispatch(intent: SettingsIntent) {
        viewModelScope.launch {
            when (intent) {
                is SettingsIntent.UpdateBaseUrlInput -> {
                    _state.update { currentState ->
                        currentState.copy(baseUrlInput = intent.url, errorMessage = null)
                    }
                }

                is SettingsIntent.SaveBaseUrl -> saveSettings()
                is SettingsIntent.LoadInitialSettings -> loadInitialSettings()
                is SettingsIntent.ResetSaveStatus -> _state.update { it.copy(saveSuccess = false) }
            }
        }
    }

    private fun loadInitialSettings() {
        val currentBaseUrl = prefsRepo.baseUrl ?: ""
        _state.update { it.copy(baseUrlInput = currentBaseUrl) }
    }

    private fun saveSettings() {
        _state.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }

        val urlToSave = state.value.baseUrlInput.trim()
        if (urlToSave.isBlank() || !isValidUrl(urlToSave)) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Invalid URL. Please enter a valid URL (e.g., http://example.com)."
                )
            }
            viewModelScope.launch { _effect.emit(SettingsEffect.ShowError("Invalid URL")) }
            return
        }

        try {
            prefsRepo.baseUrl = urlToSave
            _state.update { it.copy(isLoading = false, saveSuccess = true, errorMessage = null) }
            viewModelScope.launch { _effect.emit(SettingsEffect.SavedSuccessfully) }
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed to save settings: ${e.message}",
                    saveSuccess = false
                )
            }
            viewModelScope.launch { _effect.emit(SettingsEffect.ShowError("Failed to save settings")) }
        }
    }

    private fun isValidUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            URL(url)
            url.startsWith("http://") || url.startsWith("https://")
        } catch (e: MalformedURLException) {
            false
        }
    }
}


