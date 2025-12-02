package com.slateblua.meent.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

    LaunchedEffect(Unit) {
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
    ) { padding ->
        Row(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            StreakCard(
                modifier = Modifier.weight(1f),
                streakDays = uiState.streakCount,
                text = "Current Streak",
                backgroundColor = MaterialTheme.colorScheme.primary,
                streakIcon = painterResource(id = R.drawable.stat),
                dropShadow = false,
                dropShadowColor = Color.Transparent,
                imageAlignment = Alignment.BottomStart
            )


            StreakCard(
                modifier = Modifier.weight(1f),
                streakDays = uiState.bestStreak,
                backgroundColor = Color(0xFF448AFF),
                text = "Best Streak",
                streakIcon = painterResource(id = R.drawable.fire),
                dropShadowColor = Color(0xFF0A2F8B), // deeper blue outline
                dropShadow = true,
                imageAlignment = Alignment.BottomEnd
            )
        }
    }

}


@Composable
fun StreakCard(
    streakDays: Int,
    modifier: Modifier = Modifier,
    dropShadowColor: Color = Color(0xFF0D47A1),
    backgroundColor: Color = Color(0xFF111111),
    textColor: Color = White,
    streakIcon: Painter? = null,
    text: String = "Current Streak",
    dropShadow: Boolean = true,
    imageAlignment: Alignment = Alignment.BottomEnd
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }


        if (streakIcon != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = imageAlignment
            ) {
                if (dropShadow) {
                    Image(
                        painter = streakIcon,
                        contentDescription = "Outline",
                        modifier = Modifier.size(105.dp),
                        colorFilter = ColorFilter.tint(dropShadowColor)
                    )
                }
                Image(
                    painter = streakIcon,
                    contentDescription = "Image",
                    modifier = Modifier.size(if (dropShadow) 84.dp else 105.dp)
                )
            }
        }
    }
}
