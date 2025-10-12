package com.slateblua.meent.feature.welcome

import androidx.lifecycle.ViewModel
import com.slateblua.meent.data.datastore.UserPreferencesRepo
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class WelcomeViewModel(
    private val userPreferencesRepo: UserPreferencesRepo
) : ViewModel(), ContainerHost<OnboardingState, OnboardingSideEffect> {

    override val container: Container<OnboardingState, OnboardingSideEffect> = container(
        OnboardingState()
    ) {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() = intent {
        val completed = userPreferencesRepo.onboardingCompletedFlow.first()
        if (completed) {
            reduce {
                state.copy(isLoading = false, isOnboardingCompleted = true)
            }

            postSideEffect(OnboardingSideEffect.NavigateToMainApp)

        } else {
            reduce {
                state.copy(isLoading = false, isOnboardingCompleted = false)
            }
        }
    }

    fun completeOnboarding() = intent {
        reduce {
            state.copy(isLoading = true)
        }

        userPreferencesRepo.updateOnboardingCompleted(true)

        reduce {
            state.copy(isLoading = false, isOnboardingCompleted = true)
        }

        postSideEffect(OnboardingSideEffect.NavigateToMainApp)
    }
}
