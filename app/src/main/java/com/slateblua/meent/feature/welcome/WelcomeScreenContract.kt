package com.slateblua.meent.feature.welcome


data class OnboardingState(
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean = false
)


sealed class OnboardingEvent {
    object LoadOnboardingStatus : OnboardingEvent()
    object CompleteOnboarding : OnboardingEvent()
}

sealed class OnboardingSideEffect {
    object NavigateToMainApp : OnboardingSideEffect()
}

