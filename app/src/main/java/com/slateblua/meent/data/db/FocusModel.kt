package com.slateblua.meent.data.db

data class FocusModel(
    val focusId: Long = 0,
    val start: Long,
    val end: Long,
    val planned: Int,
)

enum class FocusStatus {
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED,
    IDLE,
}
