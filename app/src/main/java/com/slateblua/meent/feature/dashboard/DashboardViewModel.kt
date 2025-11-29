package com.slateblua.meent.feature.dashboard

import androidx.lifecycle.ViewModel
import com.slateblua.meent.data.FocusModel
import com.slateblua.meent.data.FocusRepo
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Calendar

class DashboardViewModel(private val focusRepo: FocusRepo) : ViewModel(), ContainerHost<DashboardState, DashboardSideEffect> {

    override val container: Container<DashboardState, DashboardSideEffect> =
        container(DashboardState())

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            focusRepo.getAll().collect { sessions ->
                if (sessions.isEmpty()) {
                    reduce {
                        state.copy(
                            isLoading = false,
                            bestStreak = 0,
                            streakCount = 0,
                        )
                    }
                } else {
                    val (currentStreak, bestStreak) = calculateStreaks(sessions)

                    reduce {
                        state.copy(
                            isLoading = false,
                            bestStreak = bestStreak,
                            streakCount = currentStreak,
                        )
                    }
                }
            }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false) }
            postSideEffect(DashboardSideEffect.ShowError("Failed to load"))
        }
    }


    private fun calculateStreaks(sessions: List<FocusModel>): Pair<Int, Int> {
        if (sessions.isEmpty()) return Pair(0, 0)

        val daysWithSessions = mutableSetOf<String>()
        val calendar = Calendar.getInstance()

        sessions.forEach { session ->
            calendar.timeInMillis = session.start
            val dayKey = "%04d-%02d-%02d".format(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            daysWithSessions.add(dayKey)
        }

        val sortedDays = daysWithSessions.sorted().reversed()
        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0
        var lastDate: String? = null

        sortedDays.forEach { dayKey ->
            calendar.timeInMillis = System.currentTimeMillis()
            val todayKey = "%04d-%02d-%02d".format(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            if (lastDate == null) {
                // Start from today or yesterday
                if (dayKey == todayKey) {
                    tempStreak = 1
                    currentStreak = 1
                } else {
                    val parsedDate = Calendar.getInstance().apply {
                        set(dayKey.substring(0, 4).toInt(), dayKey.substring(5, 7).toInt() - 1, dayKey.substring(8, 10).toInt())
                    }
                    val today = Calendar.getInstance()
                    if (parsedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                        tempStreak = 1
                        currentStreak = 1
                    }
                }
            } else if (isConsecutiveDay(dayKey, lastDate)) {
                tempStreak++
                if (currentStreak == tempStreak - 1) {
                    currentStreak = tempStreak
                }
            } else {
                bestStreak = maxOf(bestStreak, tempStreak)
                tempStreak = 1
            }
            lastDate = dayKey
        }

        bestStreak = maxOf(bestStreak, tempStreak)

        return Pair(currentStreak, bestStreak)
    }

    private fun isConsecutiveDay(day1: String, day2: String): Boolean {
        return try {
            val cal1 = Calendar.getInstance().apply {
                set(day1.take(4).toInt(), day1.substring(5, 7).toInt() - 1, day1.substring(8, 10).toInt())
            }
            val cal2 = Calendar.getInstance().apply {
                set(day2.take(4).toInt(), day2.substring(5, 7).toInt() - 1, day2.substring(8, 10).toInt())
            }
            cal1.add(Calendar.DAY_OF_YEAR, 1)
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        } catch (e: Exception) {
            false
        }
    }
}