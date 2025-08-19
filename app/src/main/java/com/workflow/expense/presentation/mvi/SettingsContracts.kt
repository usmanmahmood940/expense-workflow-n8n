package com.workflow.expense.presentation.mvi

data class SettingsState(
    val baseUrlInput: String = "", // Current text in the base URL TextField
    val isLoading: Boolean = false,    // To show a loading indicator while saving
    val saveSuccess: Boolean = false,  // To indicate if saving was successful
    val errorMessage: String? = null   // To display any error messages
)

sealed class SettingsIntent {
    data class UpdateBaseUrlInput(val url: String) : SettingsIntent() // User types in the TextField
    object SaveBaseUrl : SettingsIntent()                             // User clicks "Save"
    object LoadInitialSettings :
        SettingsIntent()                     // To load current settings on init

    object ResetSaveStatus : SettingsIntent()                         // To reset saveSuccess for UI
}