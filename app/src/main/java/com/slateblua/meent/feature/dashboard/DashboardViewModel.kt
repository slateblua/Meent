package com.slateblua.meent.feature.dashboard

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class DashboardViewModel() : ViewModel(), ContainerHost<DashboardState, DashboardSideEffect> {

    override val container: Container<DashboardState, DashboardSideEffect> = container(
        DashboardState()
    ) {
    }
}
