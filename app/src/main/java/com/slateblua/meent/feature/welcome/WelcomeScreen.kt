package com.slateblua.meent.feature.welcome
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.slateblua.meent.core.MAIN_APP_CONTENT
import com.slateblua.meent.core.ONBOARDING
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: WelcomeViewModel = koinViewModel()
) {
    val uiState by viewModel.container.stateFlow.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collectLatest { effect ->
            when (effect) {
                is OnboardingSideEffect.NavigateToMainApp -> {
                    navController.navigate(MAIN_APP_CONTENT) {
                        popUpTo(ONBOARDING) { inclusive = true }
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading && !uiState.isOnboardingCompleted -> {
                CircularProgressIndicator()
            }

            !uiState.isOnboardingCompleted -> {
                OnboardingContent(
                    onGetStarted = { viewModel.completeOnboarding() }
                )
            }

            uiState.isOnboardingCompleted -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun OnboardingContent(
    onGetStarted: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_info_details),
            contentDescription = "Onboarding illustration",
            modifier = Modifier
                .size(140.dp)
                .padding(top = 16.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to Meent!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Your personal productivity companion.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureItem(
                icon = android.R.drawable.ic_menu_today,
                title = "Plan Your Day",
                description = "Don't forget to stay organized."
            )

            FeatureItem(
                icon = android.R.drawable.ic_menu_my_calendar,
                title = "Track Progress",
                description = "Visualize your achievements and productivity trends."
            )

            FeatureItem(
                icon = android.R.drawable.ic_menu_agenda,
                title = "Stay Motivated",
                description = "Daily insights help you stay focused and consistent."
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGetStarted,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                "Get Started",
                style = MaterialTheme.typography.titleMedium
            )
        }


        Text(
            // Should we add any?
            text = "By continuing, you agree to our Terms & Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 12.dp),
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun FeatureItem(
    icon: Int,
    title: String,
    description: String
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
