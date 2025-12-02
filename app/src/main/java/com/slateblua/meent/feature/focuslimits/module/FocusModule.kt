package com.slateblua.meent.feature.focuslimits.module

import com.slateblua.meent.feature.focuslimits.FocusViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val focusModule = module {
    viewModel { FocusViewModel(get(), androidApplication())}
}