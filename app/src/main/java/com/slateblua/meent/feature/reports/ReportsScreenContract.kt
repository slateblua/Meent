package com.slateblua.meent.feature.reports

data class ReportsState(
    val isLoading: Boolean = false,
    // Basic metrics
    val totalSessions: Int = 0,
    val totalFocusedMinutes: Int = 0,
    val averageSessionDuration: Int = 0,
    val todaySessionsCount: Int = 0,
    val longestSession: Int = 0,
    // Advanced metrics
    val weeklySessionsCount: Int = 0,
    val weeklyFocusedMinutes: Int = 0,
    val monthlySessionsCount: Int = 0,
    val monthlyFocusedMinutes: Int = 0,
    val currentStreak: Int = 0, // Days in a row with at least 1 session
    val bestStreak: Int = 0,
    val dailyAverageThisWeek: Int = 0, // Average sessions per day this week
    val dailyBreakdown: Map<String, Int> = emptyMap(), // Day name to session count
    val selectedTimeRange: Range = Range.WEEK,
)

enum class Range {
    WEEK,
    MONTH,
    ALL_TIME
}

sealed class ReportsSideEffect {
    data class ShowError(val message: String) : ReportsSideEffect()
}
