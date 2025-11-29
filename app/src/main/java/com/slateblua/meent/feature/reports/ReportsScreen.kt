package com.slateblua.meent.feature.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slateblua.meent.feature.dashboard.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = koinViewModel()
) {
    val uiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.container.sideEffectFlow) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {
                is ReportsSideEffect.ShowError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = it.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { scaffoldPadding ->
        if (uiState.isLoading && uiState.totalSessions == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with time range selector
                HeaderSection(uiState, viewModel)

                if (uiState.totalSessions == 0) {
                    // Empty state
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = "Start your first focus session to see analytics!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                } else {
                    // Analytics cards in a 2x2 grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnalyticsCard(
                            title = "Total Sessions",
                            value = uiState.totalSessions.toString(),
                            icon = Icons.Filled.Favorite,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsCard(
                            title = "Focused Time",
                            value = formatMinutes(uiState.totalFocusedMinutes),
                            icon = Icons.Filled.Timer,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AnalyticsCard(
                            title = "Avg Duration",
                            value = formatMinutes(uiState.averageSessionDuration),
                            icon = Icons.Filled.Schedule,
                            modifier = Modifier.weight(1f)
                        )
                        AnalyticsCard(
                            title = "Longest Session",
                            value = formatMinutes(uiState.longestSession),
                            icon = Icons.Filled.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Streak card
                    StreakCard(uiState = uiState)

                    // Weekly breakdown
                    WeeklyBreakdownCard(uiState = uiState)

                    // Trend indicator
                    TrendCard(uiState = uiState)

                    // Time-specific metrics
                    TimeSpecificMetrics(uiState = uiState)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(uiState: ReportsState, viewModel: ReportsViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Your Focus Analytics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Time range selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeRangeButton(
                label = "Week",
                selected = uiState.selectedTimeRange == Range.WEEK,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeTimeRange(Range.WEEK)
            }
            TimeRangeButton(
                label = "Month",
                selected = uiState.selectedTimeRange == Range.MONTH,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeTimeRange(Range.MONTH)
            }
            TimeRangeButton(
                label = "All Time",
                selected = uiState.selectedTimeRange == Range.ALL_TIME,
                modifier = Modifier.weight(1f)
            ) {
                viewModel.changeTimeRange(Range.ALL_TIME)
            }
        }
    }
}

@Composable
private fun TimeRangeButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun StreakCard(uiState: ReportsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🔥 Current Streak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Best: ${uiState.bestStreak} days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = "${uiState.currentStreak} days",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun WeeklyBreakdownCard(uiState: ReportsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Daily breakdown bars
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.dailyBreakdown.forEach { (day, count) ->
                    DayBreakdownBar(day, count)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Weekly Total: ${uiState.weeklySessionsCount} sessions",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatMinutes(uiState.weeklyFocusedMinutes),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DayBreakdownBar(day: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(30.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    color = if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.width(20.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TrendCard(uiState: ReportsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (uiState.sessionTrend >= 0) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "vs last week",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (uiState.sessionTrend >= 0) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                    contentDescription = "Trend",
                    tint = if (uiState.sessionTrend >= 0) Color.Green else Color.Red,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "%+.0f%%".format(uiState.sessionTrend),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TimeSpecificMetrics(uiState: ReportsState) {
    when (uiState.selectedTimeRange) {
        Range.WEEK -> {
            WeekMetricsCards(uiState)
        }
        Range.MONTH -> {
            MonthMetricsCards(uiState)
        }
        Range.ALL_TIME -> {
            AllTimeMetricsCards(uiState)
        }
    }
}

@Composable
private fun WeekMetricsCards(uiState: ReportsState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnalyticsCard(
            title = "This Week",
            value = formatMinutes(uiState.weeklyFocusedMinutes),
            icon = Icons.Filled.Timer,
            modifier = Modifier.weight(1f)
        )
        AnalyticsCard(
            title = "Today",
            value = uiState.todaySessionsCount.toString(),
            icon = Icons.Filled.Favorite,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun MonthMetricsCards(uiState: ReportsState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnalyticsCard(
            title = "This Month",
            value = formatMinutes(uiState.monthlyFocusedMinutes),
            icon = Icons.Filled.Timer,
            modifier = Modifier.weight(1f)
        )
        AnalyticsCard(
            title = "Sessions",
            value = uiState.monthlySessionsCount.toString(),
            icon = Icons.Filled.Favorite,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AllTimeMetricsCards(uiState: ReportsState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnalyticsCard(
            title = "Total Time",
            value = formatMinutes(uiState.totalFocusedMinutes),
            icon = Icons.Filled.Timer,
            modifier = Modifier.weight(1f)
        )
        AnalyticsCard(
            title = "All Sessions",
            value = uiState.totalSessions.toString(),
            icon = Icons.Filled.Favorite,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatMinutes(minutes: Int): String {
    return if (minutes >= 60) {
        val hours = minutes / 60
        val mins = minutes % 60
        if (mins > 0) "$hours h ${mins}m" else "$hours h"
    } else {
        "${minutes}m"
    }
}