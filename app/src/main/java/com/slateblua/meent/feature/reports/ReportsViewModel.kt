package com.slateblua.meent.feature.reports

import androidx.lifecycle.ViewModel
import com.slateblua.meent.data.FocusModel
import com.slateblua.meent.data.FocusRepo
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import java.util.Calendar

class ReportsViewModel(
    private val focusRepo: FocusRepo
) : ViewModel(), ContainerHost<ReportsState, ReportsSideEffect> {

    override val container: Container<ReportsState, ReportsSideEffect> = container(
        ReportsState()
    ) {
        loadAnalytics()
    }

    fun changeTimeRange(timeRange: Range) = intent {
        reduce { state.copy(selectedTimeRange = timeRange, isLoading = true) }
        loadAnalytics()
    }

    private fun loadAnalytics() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            focusRepo.getAll().collect { sessions ->
                if (sessions.isEmpty()) {
                    reduce {
                        state.copy(
                            isLoading = false,
                            totalSessions = 0,
                            totalFocusedMinutes = 0,
                            averageSessionDuration = 0,
                            todaySessionsCount = 0,
                            longestSession = 0,
                            weeklySessionsCount = 0,
                            weeklyFocusedMinutes = 0,
                            monthlySessionsCount = 0,
                            monthlyFocusedMinutes = 0,
                            currentStreak = 0,
                            bestStreak = 0,
                            sessionTrend = 0f,
                            dailyAverageThisWeek = 0,
                            dailyBreakdown = emptyMap()
                        )
                    }
                } else {
                    val now = Calendar.getInstance()

                    // Calculate basic metrics
                    val totalSessions = sessions.size
                    val totalMinutes = sessions.sumOf { (it.end - it.start) / (1000 * 60) }.toInt()
                    val averageDuration = if (totalSessions > 0) totalMinutes / totalSessions else 0
                    val longestSession = sessions.maxOfOrNull { (it.end - it.start) / (1000 * 60) }?.toInt() ?: 0

                    // Today's metrics
                    val (todayStart, todayEnd) = getDateRange(now, 0)
                    val todaySessionCount = sessions.count { it.start in todayStart..todayEnd }

                    // Weekly metrics
                    val (weekStart, weekEnd) = getWeekRange(now)
                    val weekSessions = sessions.filter { it.start in weekStart..weekEnd }
                    val weeklySessionsCount = weekSessions.size
                    val weeklyFocusedMinutes = weekSessions.sumOf { (it.end - it.start) / (1000 * 60) }.toInt()
                    val dailyAverageThisWeek = if (weeklySessionsCount > 0) weeklySessionsCount / 7 else 0

                    // Monthly metrics
                    val (monthStart, monthEnd) = getMonthRange(now)
                    val monthSessions = sessions.filter { it.start in monthStart..monthEnd }
                    val monthlySessionsCount = monthSessions.size
                    val monthlyFocusedMinutes = monthSessions.sumOf { (it.end - it.start) / (1000 * 60) }.toInt()

                    // Session trend (this week vs last week)
                    val lastWeekStart = weekStart - (7 * 24 * 60 * 60 * 1000)
                    val lastWeekEnd = weekStart - 1
                    val lastWeekSessions = sessions.count { it.start in lastWeekStart..lastWeekEnd }
                    val sessionTrend = if (lastWeekSessions > 0) {
                        ((weeklySessionsCount - lastWeekSessions).toFloat() / lastWeekSessions) * 100
                    } else {
                        if (weeklySessionsCount > 0) 100f else 0f
                    }

                    // Streak calculation
                    val (currentStreak, bestStreak) = calculateStreaks(sessions)

                    // Daily breakdown for the week
                    val dailyBreakdown = calculateDailyBreakdown(weekSessions, weekStart)

                    reduce {
                        state.copy(
                            isLoading = false,
                            totalSessions = totalSessions,
                            totalFocusedMinutes = totalMinutes,
                            averageSessionDuration = averageDuration,
                            todaySessionsCount = todaySessionCount,
                            longestSession = longestSession,
                            weeklySessionsCount = weeklySessionsCount,
                            weeklyFocusedMinutes = weeklyFocusedMinutes,
                            monthlySessionsCount = monthlySessionsCount,
                            monthlyFocusedMinutes = monthlyFocusedMinutes,
                            currentStreak = currentStreak,
                            bestStreak = bestStreak,
                            sessionTrend = sessionTrend,
                            dailyAverageThisWeek = dailyAverageThisWeek,
                            dailyBreakdown = dailyBreakdown
                        )
                    }
                }
            }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false) }
            postSideEffect(ReportsSideEffect.ShowError("Failed to load analytics"))
        }
    }

    private fun getDateRange(calendar: Calendar, dayOffset: Int): Pair<Long, Long> {
        val cal = calendar.clone() as Calendar
        cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    private fun getWeekRange(calendar: Calendar): Pair<Long, Long> {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.DAY_OF_WEEK, 6)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
    }

    private fun getMonthRange(calendar: Calendar): Pair<Long, Long> {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis

        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.DAY_OF_MONTH, -1)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val end = cal.timeInMillis

        return Pair(start, end)
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

    private fun calculateDailyBreakdown(sessions: List<FocusModel>, weekStart: Long): Map<String, Int> {
        val breakdown = mutableMapOf<String, Int>()
        val dayNames = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = weekStart

        for (i in 0..6) {
            val dayName = dayNames[calendar.get(Calendar.DAY_OF_WEEK) - 1]
            val (dayStart, dayEnd) = getDateRange(calendar, 0)
            val count = sessions.count { it.start in dayStart..dayEnd }
            breakdown[dayName] = count
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return breakdown
    }
}
