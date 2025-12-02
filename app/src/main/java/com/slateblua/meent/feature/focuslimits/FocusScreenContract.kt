package com.slateblua.meent.feature.focuslimits

import android.content.pm.ApplicationInfo
import com.slateblua.meent.data.FocusStatus

data class FocusUiState(
    val remainingTimeMillis: Long = 0,
    val plannedDurationMinutes: Int = 25,
    val status: FocusStatus = FocusStatus.IDLE,
    val availableApps: List<ApplicationInfo> = emptyList(),
    val blockedApps: Set<String> = emptySet(),
    val showAppSelection: Boolean = false
)

sealed class FocusSideEffect {
    data class SessionCompletedFeedback(val message: String) : FocusSideEffect()
}
