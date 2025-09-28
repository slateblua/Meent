package com.slateblua.meent.data.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalDataSource (private val dao: FocusDao) : LocalDataSource {
    override suspend fun add(focusModel: FocusModel) {
        dao.add(focusModel.toEntry())
    }

    override suspend fun update(focusModel: FocusModel) {
        dao.update(focusModel.toEntry())
    }

    override suspend fun getById(focusModel: Long): Flow<FocusModel> {
        return dao.getById(focusModel).map { it.toModel() }
    }

    override fun getAll(): Flow<List<FocusModel>> {
        return dao.getAll().map {
            it.map {
                entry -> entry.toModel()
            }
        }
    }

    override fun getForDateRange(
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