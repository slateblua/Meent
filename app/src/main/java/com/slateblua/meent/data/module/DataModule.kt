package com.slateblua.meent.data.module

import androidx.room.Room
import com.slateblua.meent.data.datastore.UserPreferencesRepo
import com.slateblua.meent.data.db.AppDatabase
import com.slateblua.meent.data.db.FocusRepo
import com.slateblua.meent.data.db.FocusRepoImpl
import com.slateblua.meent.data.db.LocalDataSource
import com.slateblua.meent.data.db.RoomLocalDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "meent_database",
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    single {
        get<AppDatabase>().focusDao()
    }

    single {
        UserPreferencesRepo(androidContext())
    }

    // Binds RoomLocalDataSource to LocalDataSource
    single { RoomLocalDataSource(get()) } bind LocalDataSource::class

    // Binds MoodRepoImpl to MoodRepoImpl
    single { FocusRepoImpl(get()) } bind FocusRepo::class
}
