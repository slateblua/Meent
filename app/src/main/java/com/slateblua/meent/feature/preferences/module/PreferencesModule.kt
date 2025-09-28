package com.slateblua.meent.feature.preferences.module

import com.slateblua.meent.feature.preferences.PreferencesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { PreferencesViewModel(get()) }
}