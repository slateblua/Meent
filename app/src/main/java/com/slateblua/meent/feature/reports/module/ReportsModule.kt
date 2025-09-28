package com.slateblua.meent.feature.reports.module

import com.slateblua.meent.feature.reports.ReportsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val reportsModule = module {
    viewModel { ReportsViewModel(get()) }
}