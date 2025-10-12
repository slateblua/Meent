package com.slateblua.meent.data

import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun addFocus(focusModel: FocusModel)

    suspend fun updateFocus(focusModel: FocusModel)

    suspend fun getFocusById(focusId: Long): Flow<FocusModel>

    fun getAllFocusModels(): Flow<List<FocusModel>>

    fun getFocusModelsForDateRange(start: Long, end: Long): Flow<List<FocusModel>>
}