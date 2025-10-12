package com.slateblua.meent.feature.focuslimits

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slateblua.meent.data.FocusStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusLimitsScreen(
    modifier: Modifier = Modifier,
    viewModel: FocusViewModel = koinViewModel()
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = state.plannedDurationMinutes / 60,
        initialMinute = state.plannedDurationMinutes % 60,
        is24Hour = true
    )

    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = "Select duration",
                        style = MaterialTheme.typography.labelMedium
                    )
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { showTimePicker = false }
                        ) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                val totalMinutes = timePickerState.hour * 60 + timePickerState.minute
                                viewModel.setPlannedDuration(totalMinutes)
                                showTimePicker = false
                            }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.container.sideEffectFlow) {
        viewModel.container.sideEffectFlow.collectLatest {
            when (it) {
                is FocusSideEffect.SessionCompletedFeedback -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = it.message, duration = SnackbarDuration.Short)
                    }
                }
            }
        }
    }
    val minutes = state.remainingTimeMillis / 1000 / 60
    val seconds = (state.remainingTimeMillis / 1000) % 60

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding) // Use padding from Scaffold
                .padding(16.dp), // Additional screen padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                val progressAs by animateFloatAsState(
                    targetValue = if (state.status == FocusStatus.ACTIVE) {
                        state.remainingTimeMillis / (state.plannedDurationMinutes * 60_000f)
                    } else {
                        0f
                    },
                    label = "progress"
                )

                if (state.status == FocusStatus.ACTIVE || state.status == FocusStatus.PAUSED) {
                    CircularProgressIndicator(
                        progress = { progressAs },
                        modifier = Modifier.size(220.dp),
                        color = ProgressIndicatorDefaults.circularColor,
                        strokeWidth = 12.dp,
                        trackColor = ProgressIndicatorDefaults.circularTrackColor,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    )
                }

                Text(
                    text = String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (state.status != FocusStatus.ACTIVE) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Duration: ${state.plannedDurationMinutes} minutes",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(onClick = { showTimePicker = true }) {
                        Text("Set Duration")
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                when (state.status) {
                    FocusStatus.ACTIVE -> {
                        Button(
                            onClick = { viewModel.pauseFocusSession() },
                        ) {
                            Text("Pause")
                        }

                        OutlinedButton(onClick = { viewModel.stopFocusSession() }) {
                            Text("Stop")
                        }
                    }

                    FocusStatus.PAUSED -> {
                        Button(onClick = { viewModel.resumeFocusSession() }) {
                            Text("Resume")
                        }
                        OutlinedButton(onClick = { viewModel.stopFocusSession() }) {
                            Text("Stop")
                        }
                    }

                    else  -> {
                        Button(onClick = { viewModel.startFocusSession() }) {
                            Text("Start")
                        }
                    }
                }
            }
        }
    }
}