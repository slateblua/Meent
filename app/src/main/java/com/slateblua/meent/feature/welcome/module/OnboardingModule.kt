package com.slateblua.meent.feature.welcome.module

import com.slateblua.meent.feature.welcome.WelcomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel { WelcomeViewModel(get()) }
}