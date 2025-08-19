package com.workflow.expense.presentation

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workflow.expense.domain.repository.SharedPrefRepo
import com.workflow.expense.presentation.mvi.SettingsIntent
import com.workflow.expense.presentation.mvi.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URL

class SettingsViewModel(private val prefsRepo: SharedPrefRepo) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        // Automatically process the LoadInitialSettings intent when ViewModel is created
        processIntent(SettingsIntent.LoadInitialSettings)
    }

    fun processIntent(intent: SettingsIntent) {
        viewModelScope.launch { // Launch a coroutine for asynchronous operations
            when (intent) {
                is SettingsIntent.UpdateBaseUrlInput -> {
                    _uiState.update { currentState ->
                        currentState.copy(baseUrlInput = intent.url, errorMessage = null)
                    }
                }

                is SettingsIntent.SaveBaseUrl -> {
                    saveSettings()
                }

                is SettingsIntent.LoadInitialSettings -> {
                    loadInitialSettings()
                }

                is SettingsIntent.ResetSaveStatus -> {
                    _uiState.update { it.copy(saveSuccess = false) }
                }
            }
        }
    }

    private fun loadInitialSettings() {
        val currentBaseUrl = prefsRepo.baseUrl ?: "" // Get current URL or default to empty
        _uiState.update { currentState ->
            currentState.copy(baseUrlInput = currentBaseUrl)
        }
    }

    private fun saveSettings() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }

        val urlToSave = _uiState.value.baseUrlInput.trim()

        if (urlToSave.isBlank() || !isValidUrl(urlToSave)) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Invalid URL. Please enter a valid URL (e.g., http://example.com)."
                )
            }
            return
        }

        try {
            prefsRepo.baseUrl = urlToSave
            _uiState.update {
                it.copy(isLoading = false, saveSuccess = true, errorMessage = null)
            }
        } catch (e: Exception) {
            // Handle any potential exceptions during save, though SharedPrefs is usually robust
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed to save settings: ${e.message}",
                    saveSuccess = false
                )
            }
        }
    }

    // Basic URL validation (can be more sophisticated)
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