package com.slateblua.meent.feature.welcome


data class OnboardingState(
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean = false
)

sealed class OnboardingSideEffect {
    object NavigateToMainApp : OnboardingSideEffect()
}

