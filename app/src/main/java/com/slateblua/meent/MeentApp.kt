package com.slateblua.meent

import android.app.Application
import com.slateblua.meent.data.module.databaseModule
import com.slateblua.meent.feature.dashboard.module.dashboardModule
import com.slateblua.meent.feature.focuslimits.module.focusModule
import com.slateblua.meent.feature.welcome.module.onboardingModule
import com.slateblua.meent.feature.reports.module.reportsModule
import com.slateblua.meent.feature.preferences.module.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MeentApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MeentApp)
            androidLogger()
            modules(
                databaseModule,
                dashboardModule,
                settingsModule,
                focusModule,
                reportsModule,
                onboardingModule,
            )
        }
    }
}
