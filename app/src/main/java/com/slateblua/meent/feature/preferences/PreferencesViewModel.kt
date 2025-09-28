package com.slateblua.meent.feature.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slateblua.meent.data.datastore.UserPreferencesRepos
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class PreferencesViewModel(
    private val userPreferencesRepos: UserPreferencesRepos
) : ViewModel(), ContainerHost<PreferencesState, PreferencesSideEffect> {

    override val container: Container<PreferencesState, PreferencesSideEffect> = container(PreferencesState()) {
        // Load initial theme status
        loadInitialThemeStatus()
    }

    private fun loadInitialThemeStatus() = intent {
        userPreferencesRepos.darkThemeEnabledFlow.collect { isEnabled ->
            reduce { state.copy(isDarkThemeEnabled = isEnabled, isLoading = false) }
        }
    }

    fun toggleDarkTheme(isEnabled: Boolean) = intent {
        reduce { state.copy(isLoading = true) } // Show loading while saving
        userPreferencesRepos.updateDarkThemeEnabled(isEnabled)
    }

    val isDarkThemeEnabled: StateFlow<Boolean> = userPreferencesRepos.darkThemeEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
