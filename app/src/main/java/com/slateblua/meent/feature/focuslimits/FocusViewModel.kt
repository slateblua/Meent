package com.slateblua.meent.feature.focuslimits

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slateblua.meent.core.services.AppBlockerService
import com.slateblua.meent.data.FocusModel
import com.slateblua.meent.data.FocusRepo
import com.slateblua.meent.data.FocusStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Date

class FocusViewModel(
    // refactor to use use-cases later
    private val focusRepo: FocusRepo,
    private val application: Application
) : ViewModel(), ContainerHost<FocusUiState, FocusSideEffect> {

    override val container: Container<FocusUiState, FocusSideEffect> = container(FocusUiState()) {
        loadInstalledApps()
    }

    private var timerJob: Job? = null

    private fun loadInstalledApps() = intent {
        withContext(Dispatchers.IO) {
            val pm = application.packageManager
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { app ->
                    (app.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0 ||
                            (app.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
                }
                .filter { it.packageName != application.packageName } // Exclude self
                .sortedBy { it.loadLabel(pm).toString() }
            
            reduce { state.copy(availableApps = apps) }
        }
    }

    fun toggleAppSelectionVisibility(show: Boolean) = intent {
        reduce { state.copy(showAppSelection = show) }
    }

    fun toggleAppBlock(packageName: String) = intent {
        val currentBlocked = state.blockedApps.toMutableSet()
        if (currentBlocked.contains(packageName)) {
            currentBlocked.remove(packageName)
        } else {
            currentBlocked.add(packageName)
        }
        reduce { state.copy(blockedApps = currentBlocked) }
    }

    fun setPlannedDuration(minutes: Int) = intent {
        reduce {
            state.copy(
                plannedDurationMinutes = minutes,
                remainingTimeMillis = minutes * 60 * 1000L
            )
        }
    }

    fun startFocusSession() = intent {
        val durationMillis = state.plannedDurationMinutes * 60 * 1000L
        
        // Update service
        AppBlockerService.blockedApps.clear()
        AppBlockerService.blockedApps.addAll(state.blockedApps)
        
        reduce { state.copy(status = FocusStatus.ACTIVE, remainingTimeMillis = durationMillis) }
        startTimer(durationMillis)
    }

    private fun startTimer(durationMillis: Long) {
        timerJob?.cancel()
        timerJob = flow {
            var remaining = durationMillis
            while (remaining > 0) {
                emit(remaining)
                delay(1000)
                remaining -= 1000
            }
            emit(0)
        }.onEach { remaining ->
            intent {
                reduce { state.copy(remainingTimeMillis = remaining) }
                if (remaining == 0L) {
                    completeFocusSession()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun pauseFocusSession() = intent {
        timerJob?.cancel()
        reduce { state.copy(status = FocusStatus.PAUSED) }
    }

    fun resumeFocusSession() = intent {
        startTimer(state.remainingTimeMillis)
        reduce { state.copy(status = FocusStatus.ACTIVE) }
    }

    fun stopFocusSession() = intent {
        timerJob?.cancel()
        AppBlockerService.blockedApps.clear()
        reduce { state.copy(status = FocusStatus.IDLE, remainingTimeMillis = state.plannedDurationMinutes * 60 * 1000L) }
    }

    private fun completeFocusSession() = intent {
        AppBlockerService.blockedApps.clear()
        focusRepo.add(
            FocusModel(
                start = Date().time - state.plannedDurationMinutes * 60 * 1000L,
                end = Date().time,
                planned = state.plannedDurationMinutes,
            )
        )
        reduce { state.copy(status = FocusStatus.COMPLETED, remainingTimeMillis = state.plannedDurationMinutes * 60 * 1000L) }
        postSideEffect(FocusSideEffect.SessionCompletedFeedback("Focus session completed!") )
    }
}
