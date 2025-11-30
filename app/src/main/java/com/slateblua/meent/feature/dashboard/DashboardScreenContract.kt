package com.slateblua.meent.feature.dashboard

data class DashboardState(
    val isLoading: Boolean = false,
    val streakCount: Int = 0,
    val bestStreak: Int = 0,
)

sealed class DashboardSideEffect {
    data class ShowError(val message: String) : DashboardSideEffect()
}