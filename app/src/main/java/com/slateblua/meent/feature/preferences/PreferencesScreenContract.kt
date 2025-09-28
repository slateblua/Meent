package com.slateblua.meent.feature.preferences

// State for the Settings screen
data class PreferencesState(
    val isDarkThemeEnabled: Boolean = false,
    val isLoading: Boolean = true // To wait for initial theme status load
)

// Side Effects for the Settings screen (if any needed in the future)
sealed class PreferencesSideEffect {
    // data class ShowMessage(val message: String) : SettingsSideEffect()
}
