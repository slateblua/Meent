package com.slateblua.meent.feature.reports

data class ReportsState(
    val isLoading: Boolean = false,
    // Basic metrics
    val totalSessions: Int = 0,
    val totalFocusedMinutes: Int = 0,
    val averageSessionDuration: Int = 0,
    val longestSession: Int = 0,
    // Advanced metrics
    val weeklySessionsCount: Int = 0,
    val weeklyFocusedMinutes: Int = 0,
    val monthlySessionsCount: Int = 0,
    val monthlyFocusedMinutes: Int = 0,
    val selectedTimeRange: Range = Range.WEEK,
)

enum class Range(val label: String) {
    WEEK("Week"),
    MONTH("Month"),
    ALL_TIME("All time")
}

sealed class ReportsSideEffect {
    data class ShowError(val message: String) : ReportsSideEffect()
}
