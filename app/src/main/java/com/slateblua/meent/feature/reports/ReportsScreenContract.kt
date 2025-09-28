package com.slateblua.meent.feature.reports

import com.slateblua.meent.data.db.FocusEntry
import java.util.Calendar

// State for the Reports screen
data class ReportsState(
    val selectedDate: Calendar = Calendar.getInstance(),
    val selectedDateFormatted: String = "", // Will be derived from selectedDate
    val sessionsForSelectedDate: List<FocusEntry> = emptyList(),
    val isLoading: Boolean = false
)

// Side Effects for the Reports screen
sealed class ReportsSideEffect {
    data class ShowError(val message: String) : ReportsSideEffect()
}
