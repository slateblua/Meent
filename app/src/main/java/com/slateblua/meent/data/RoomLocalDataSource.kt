package com.slateblua.meent.data

import com.slateblua.meent.data.db.FocusDao
import com.slateblua.meent.data.mappers.toEntry
import com.slateblua.meent.data.mappers.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalDataSource (private val dao: FocusDao) : LocalDataSource {
    override suspend fun addFocus(focusModel: FocusModel) {
        dao.add(focusModel.toEntry())
    }

    override suspend fun updateFocus(focusModel: FocusModel) {
        dao.update(focusModel.toEntry())
    }

    override suspend fun getFocusById(focusId: Long): Flow<FocusModel> {
        return dao.getById(focusId).map { it.toModel() }
    }

    override fun getAllFocusModels(): Flow<List<FocusModel>> {
        return dao.getAll().map {
            it.map {
                entry -> entry.toModel()
            }
        }
    }

    override fun getFocusModelsForDateRange(
        start: Long,
        end: Long
    ): Flow<List<FocusModel>> {
        return dao.getForDateRange(start, end).map {
            it.map {
                entry -> entry.toModel()
            }
        }
    }
}