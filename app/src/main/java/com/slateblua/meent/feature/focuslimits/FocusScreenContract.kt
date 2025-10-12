package com.slateblua.meent.feature.focuslimits

import com.slateblua.meent.data.FocusStatus

data class FocusUiState(
    val remainingTimeMillis: Long = 0,
    val plannedDurationMinutes: Int = 25,
    val status: FocusStatus = FocusStatus.IDLE,
    val currentSessionId: Long? = null
)

sealed class FocusSideEffect {
    data class SessionCompletedFeedback(val message: String) : FocusSideEffect()
    // data class ShowError(val message: String) : FocusSideEffect() // Example for later
}
