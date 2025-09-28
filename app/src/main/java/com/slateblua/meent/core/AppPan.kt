package com.slateblua.meent.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // Import IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.slateblua.meent.feature.dashboard.DashboardScreen
import com.slateblua.meent.feature.focuslimits.FocusLimitsScreen
import com.slateblua.meent.feature.welcome.OnboardingScreen
import com.slateblua.meent.feature.reports.ReportsScreen
import com.slateblua.meent.feature.preferences.SettingsScreen

const val ONBOARDING = "onboarding"
const val MAIN_APP_CONTENT = "main_app_content"
const val DASHBOARD = "dashboard"
const val FOCUS_LIMITS = "focus_limits"
const val REPORTS = "reports"
const val SETTINGS = "settings"

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : BottomNavItem(
        DASHBOARD,
        Icons.Filled.Dashboard,
        "Dashboard"
    )

    object FocusLimits : BottomNavItem(
        FOCUS_LIMITS,
        Icons.Filled.Timer,
        "Focus"
    )

    object Reports : BottomNavItem(
        REPORTS,
        Icons.Filled.BarChart,
        "Reports"
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.FocusLimits,
    BottomNavItem.Reports
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPan(
    appNavController: NavHostController
) {
    val mainContentNavController = rememberNavController()
    val appNavBackStackEntry by appNavController.currentBackStackEntryAsState()
    val currentAppRoute = appNavBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            // Only show TopAppBar if in the main app content area
            if (currentAppRoute == MAIN_APP_CONTENT) {
                TopAppBar(
                    title = { Text("Meent") },
                    actions = {
                        IconButton(onClick = { mainContentNavController.navigate(SETTINGS) }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                )
            }
        },
        bottomBar = {
            // Only show BottomNavigationBar if in the main app content area
            if (currentAppRoute == MAIN_APP_CONTENT) {
                BottomBar(navController = mainContentNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = appNavController, // NavHost uses the app-level controller
            startDestination = ONBOARDING, // Start with onboarding
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(ONBOARDING) {
                OnboardingScreen(navController = appNavController)
            }

            composable(MAIN_APP_CONTENT) {
                // Main content area after onboarding.
                // It hosts another NavHost controlled by mainContentNavController.
                AppContentPan(nacController = mainContentNavController)
            }
        }
    }
}

@Composable
fun AppContentPan(nacController: NavHostController) {
    NavHost(
        navController = nacController,
        startDestination = DASHBOARD
    ) {
        composable(DASHBOARD) {
            DashboardScreen()
        }
        composable(FOCUS_LIMITS) {
            FocusLimitsScreen()
        }
        composable(REPORTS) {
            ReportsScreen()
        }
        composable(SETTINGS) {
            SettingsScreen(navController = nacController)
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
