package com.slateblua.meent.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FocusEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusDao(): FocusDao
}
