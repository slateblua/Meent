package com.slateblua.meent.feature.reports

import androidx.lifecycle.ViewModel
import com.slateblua.meent.data.db.FocusRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val focusRepo: FocusRepo
) : ViewModel(), ContainerHost<ReportsState, ReportsSideEffect> {

    override val container: Container<ReportsState, ReportsSideEffect> = container(ReportsState()) {

    }

    private fun loadSessionsForDate(calendar: Calendar) = intent {

    }

    fun selectPreviousDay() = intent {

    }

    fun selectNextDay() = intent {

    }

    fun selectToday() = intent {
        loadSessionsForDate(Calendar.getInstance())
    }
}
