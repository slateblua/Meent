package com.slateblua.meent.data

import kotlinx.coroutines.flow.Flow

class FocusRepoImpl (private val localDataSource: LocalDataSource) : FocusRepo {
    override suspend fun add(focusModel: FocusModel) {
        localDataSource.addFocus(focusModel)
    }

    override suspend fun update(focusModel: FocusModel) {
        localDataSource.updateFocus(focusModel)
    }

    override suspend fun getById(focusModel: Long): Flow<FocusModel> {
        return localDataSource.getFocusById(focusModel);
    }

    override fun getAll(): Flow<List<FocusModel>> {
        return localDataSource.getAllFocusModels();
    }

    override fun getForDateRange(
        start: Long,
        end: Long
    ): Flow<List<FocusModel>> {
        return localDataSource.getFocusModelsForDateRange(start, end)
    }
}