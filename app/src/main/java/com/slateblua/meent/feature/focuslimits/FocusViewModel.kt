package com.slateblua.meent.feature.focuslimits

import androidx.lifecycle.ViewModel
import com.slateblua.meent.data.db.FocusRepo
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

class FocusViewModel(
    // refactor to use use-cases later
    private val focusRepo: FocusRepo,
) : ViewModel(), ContainerHost<FocusUiState, FocusSideEffect> {

    override val container: Container<FocusUiState, FocusSideEffect> = container(FocusUiState()) {

    }

    fun setPlannedDuration(minutes: Int) = intent {

    }

    fun startFocusSession() = intent {

    }

    private fun startTimer(durationMillis: Long) = intent {

    }

    fun pauseFocusSession() = intent {

    }

    fun resumeFocusSession() = intent {

    }

    fun stopFocusSession() = intent {

    }

    private fun completeFocusSession() = intent {

    }
}
