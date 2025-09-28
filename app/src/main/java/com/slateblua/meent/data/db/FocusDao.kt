package com.slateblua.meent.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Data Access Object for Focus sessions
// Room will use this to create actual implementations for database operations
@Dao
interface FocusDao {
    @Insert
    suspend fun add(session: FocusEntry): Long

    @Update
    suspend fun update(session: FocusEntry)

    @Query("SELECT * FROM focus WHERE focusId = :sessionId")
    fun getById(sessionId: Long): Flow<FocusEntry>

    @Query("SELECT * FROM focus ORDER BY start DESC")
    fun getAll(): Flow<List<FocusEntry>>

    @Query("SELECT * FROM focus WHERE start >= :start AND start < :end ORDER BY start DESC")
    fun getForDateRange(start: Long, end: Long): Flow<List<FocusEntry>>
}
