package com.slateblua.meent.feature.dashboard

data class DashboardState(
    val isLoading: Boolean = false,
    // We can add other relevant dashboard information here later,
    // e.g., daily goals, progress, etc.
)

sealed class DashboardSideEffect {
    data class ShowError(val message: String) : DashboardSideEffect()
}