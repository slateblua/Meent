package com.slateblua.meent.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slateblua.meent.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.container.sideEffectFlow) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {
                is DashboardSideEffect.ShowError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { scaffoldPadding ->
        Row(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakCard(
                modifier = Modifier.weight(1f),
                streakDays = uiState.streakCount,
                backgroundColor = MaterialTheme.colorScheme.primary
            )
            StreakCard(
                modifier = Modifier.weight(1f),
                streakDays = uiState.bestStreak,
                backgroundColor = MaterialTheme.colorScheme.tertiaryFixed,
                text = "Best Streak",
            )
        }
    }

}


@Composable
fun StreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFF111111),
    textColor: Color = Color.White,
    icon: Painter? = null,
    text: String = "Current Streak"
) {
    Card(
        modifier = modifier
            .padding(5.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocalFireDepartment,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "$streakDays",
                    style = MaterialTheme.typography.headlineMedium.copy(color = textColor)
                )
            }

            Text(
                text,
                color = textColor.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Illustration slot
        if (icon != null) {
            Image(
                alignment = Alignment.BottomEnd,
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(60.dp)
            )
        }
    }
}
