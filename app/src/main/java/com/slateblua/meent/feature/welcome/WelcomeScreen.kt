package com.slateblua.meent.feature.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.slateblua.meent.core.MAIN_APP_CONTENT
import com.slateblua.meent.core.ONBOARDING
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingScreen(
    navController: NavController, // This is the appNavController from MainScreen
    modifier: Modifier = Modifier,
    viewModel: WelcomeViewModel = koinViewModel()
) {
    val uiState by viewModel.container.stateFlow.collectAsState()

    LaunchedEffect(viewModel.container.sideEffectFlow) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {
                is OnboardingSideEffect.NavigateToMainApp -> {
                    navController.navigate(MAIN_APP_CONTENT) {
                        popUpTo(ONBOARDING) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading && !uiState.isOnboardingCompleted) {
            CircularProgressIndicator()
        } else if (!uiState.isOnboardingCompleted) {
            // Only show onboarding content if it's not completed and not just loading initial status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Welcome to Meent!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Let's get you set up to boost your productivity.")
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.completeOnboarding() }) {
                    Text("Get Started")
                }
            }
        } else {
            if (uiState.isLoading) {
                 CircularProgressIndicator()
            }
        }
    }
}
