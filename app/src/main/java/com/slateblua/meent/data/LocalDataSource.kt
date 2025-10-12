package com.slateblua.meent.data

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun add(focusModel: FocusModel)

    suspend fun update(focusModel: FocusModel)

    suspend fun getById(focusModel: Long): Flow<FocusModel>

    fun getAll(): Flow<List<FocusModel>>

    fun getForDateRange(start: Long, end: Long): Flow<List<FocusModel>>
}