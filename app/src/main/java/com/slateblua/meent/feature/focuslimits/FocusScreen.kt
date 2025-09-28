package com.slateblua.meent.feature.focuslimits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FocusLimitsScreen(
    modifier: Modifier = Modifier,
    viewModel: FocusViewModel = koinViewModel()
) {
    val uiState by viewModel.container.stateFlow.collectAsState()
    var desiredDurationInput by remember { mutableStateOf(uiState.plannedDurationMinutes.toString()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
    
    LaunchedEffect(uiState.plannedDurationMinutes) {
        if (desiredDurationInput.toIntOrNull() != uiState.plannedDurationMinutes) {
             desiredDurationInput = uiState.plannedDurationMinutes.toString()
        }
    }

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

        }
    }
}
