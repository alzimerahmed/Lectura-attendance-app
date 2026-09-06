/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.alzimerahmed.lumera.data.preferences.UserPreferences
import com.alzimerahmed.lumera.ui.screens.analytics.AnalyticsScreen
import com.alzimerahmed.lumera.ui.screens.analytics.AnalyticsViewModel
import com.alzimerahmed.lumera.ui.screens.history.AttendanceHistoryScreen
import com.alzimerahmed.lumera.ui.screens.history.HistoryViewModel
import com.alzimerahmed.lumera.ui.screens.home.HomeScreen
import com.alzimerahmed.lumera.ui.screens.home.HomeViewModel
import com.alzimerahmed.lumera.ui.screens.onboarding.OnboardingScreen
import com.alzimerahmed.lumera.ui.screens.onboarding.SetupScreen
import com.alzimerahmed.lumera.ui.screens.settings.SettingsScreen
import com.alzimerahmed.lumera.ui.screens.settings.SettingsViewModel
import com.alzimerahmed.lumera.ui.screens.subjectdetails.SubjectDetailScreen
import com.alzimerahmed.lumera.ui.screens.subjectdetails.SubjectDetailViewModel
import com.alzimerahmed.lumera.ui.screens.subjects.SubjectsScreen
import com.alzimerahmed.lumera.ui.screens.subjects.SubjectsViewModel
import com.alzimerahmed.lumera.ui.screens.timetable.TimetableScreen
import com.alzimerahmed.lumera.ui.screens.timetable.TimetableViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    userPreferences: UserPreferences,
    onCompleteOnboarding: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }
    val initialRoute = remember { if (!userPreferences.onboardingCompleted) Screen.Onboarding.route else Screen.Home.route }

    fun navigateToTab(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(Screen.Home.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navigateToTab(screen.route)
                            },
                            icon = {
                                screen.icon?.let { Icon(it, contentDescription = screen.title) }
                            },
                            label = { Text(screen.title) },
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialRoute,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) },
            popEnterTransition = { fadeIn(animationSpec = tween(150)) },
            popExitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = {
                        onCompleteOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    onSkip = {
                        onCompleteOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Setup.route) {
                val settingsVm: SettingsViewModel = getViewModel { app -> SettingsViewModel(app.repository, app.userPreferencesRepository) }
                val subjectsVm: SubjectsViewModel = getViewModel { app -> SubjectsViewModel(app.repository, app.userPreferencesRepository) }
                val prefs by settingsVm.userPreferences.collectAsState()

                SetupScreen(
                    currentApiKey = prefs.geminiApiKey,
                    onSaveApiKey = { key -> settingsVm.updateGeminiApiKey(key) },
                    onSaveSetup = { targetPercent, startDate, endDate, firstSubject, loadDemo ->
                        settingsVm.updateDefaultTargetAttendance(targetPercent)
                        if (startDate.isNotBlank() && endDate.isNotBlank()) {
                            settingsVm.updateSemesterDates(startDate, endDate)
                        }
                        if (loadDemo) {
                            settingsVm.loadDemoData {}
                        }
                        firstSubject?.let { subjectsVm.addSubject(it) }
                        onCompleteOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    },
                    onNavigateToTimetableOcr = {
                        onCompleteOnboarding()
                        navController.navigate(Screen.Timetable.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    },
                    onSkipSetup = {
                        onCompleteOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Setup.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                val homeVm: HomeViewModel = getViewModel { app -> HomeViewModel(app.repository, app.userPreferencesRepository) }
                HomeScreen(
                    viewModel = homeVm,
                    onNavigateToAddSubject = { navigateToTab(Screen.Subjects.route) },
                    onNavigateToAddTimetable = { navigateToTab(Screen.Timetable.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Timetable.route) {
                val timetableVm: TimetableViewModel = getViewModel { app -> TimetableViewModel(app.repository, app.userPreferencesRepository) }
                TimetableScreen(
                    viewModel = timetableVm,
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navigateToTab(Screen.Home.route)
                        }
                    }
                )
            }

            composable(Screen.Subjects.route) {
                val subjectsVm: SubjectsViewModel = getViewModel { app -> SubjectsViewModel(app.repository, app.userPreferencesRepository) }
                SubjectsScreen(
                    viewModel = subjectsVm,
                    onNavigateToSubjectDetail = { subId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subId))
                    },
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navigateToTab(Screen.Home.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.SubjectDetail.route,
                arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
                val detailVm: SubjectDetailViewModel = getViewModel { app -> SubjectDetailViewModel(app.repository, subjectId, app.userPreferencesRepository) }
                SubjectDetailScreen(
                    viewModel = detailVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Analytics.route) {
                val analyticsVm: AnalyticsViewModel = getViewModel { app -> AnalyticsViewModel(app.repository, app.userPreferencesRepository) }
                AnalyticsScreen(
                    viewModel = analyticsVm,
                    onNavigateToSubjectDetail = { subId ->
                        navController.navigate(Screen.SubjectDetail.createRoute(subId))
                    }
                )
            }

            composable(Screen.History.route) {
                val historyVm: HistoryViewModel = getViewModel { app -> HistoryViewModel(app.repository) }
                AttendanceHistoryScreen(viewModel = historyVm)
            }

            composable(Screen.Settings.route) {
                val settingsVm: SettingsViewModel = getViewModel { app -> SettingsViewModel(app.repository, app.userPreferencesRepository) }
                SettingsScreen(
                    viewModel = settingsVm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
