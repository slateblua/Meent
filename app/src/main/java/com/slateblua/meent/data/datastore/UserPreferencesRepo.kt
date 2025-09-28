package com.slateblua.meent.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepos(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled") // New key for dark theme
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data
        .map {
            it[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    val darkThemeEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map {
            // Default to false (light theme) if not set
            it[PreferencesKeys.DARK_THEME_ENABLED] ?: false
        }

    suspend fun updateDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.DARK_THEME_ENABLED] = enabled
        }
    }
}
