package com.slateblua.meent.feature.welcome

// State for the Onboarding screen
data class OnboardingState(
    val isLoading: Boolean = true, // To wait for DataStore read
    val isOnboardingCompleted: Boolean = false
)

// Side Effects for the Onboarding screen
sealed class OnboardingSideEffect {
    object NavigateToMainApp : OnboardingSideEffect()
}
