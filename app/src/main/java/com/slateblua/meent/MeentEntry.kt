package com.slateblua.meent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.slateblua.meent.feature.preferences.PreferencesViewModel
import com.slateblua.meent.core.AppPan
import com.slateblua.meent.core.services.AppBlockerService
import com.slateblua.meent.ui.theme.MeentTheme
import org.koin.androidx.compose.koinViewModel

class MeentEntry : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs: PreferencesViewModel = koinViewModel()

            val useDarkTheme by prefs.isDarkThemeEnabled.collectAsStateWithLifecycle()

            MeentTheme(darkTheme = useDarkTheme) { // Pass the dynamic theme preference
                val appNavController = rememberNavController()

                AppPan(appNavController = appNavController)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppBlockerService.blockedApps.clear()
    }
}