package com.slateblua.meent.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus")
data class FocusEntry(
    @PrimaryKey(autoGenerate = true) val focusId: Long = 0,
    val start: Long,
    var end: Long,
    val planned: Int,
)