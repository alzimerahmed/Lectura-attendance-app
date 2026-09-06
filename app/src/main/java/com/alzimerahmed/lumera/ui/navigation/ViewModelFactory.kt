/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alzimerahmed.lumera.LumeraApplication
import com.alzimerahmed.lumera.ui.screens.analytics.AnalyticsViewModel
import com.alzimerahmed.lumera.ui.screens.history.HistoryViewModel
import com.alzimerahmed.lumera.ui.screens.home.HomeViewModel
import com.alzimerahmed.lumera.ui.screens.settings.SettingsViewModel
import com.alzimerahmed.lumera.ui.screens.subjectdetails.SubjectDetailViewModel
import com.alzimerahmed.lumera.ui.screens.subjects.SubjectsViewModel
import com.alzimerahmed.lumera.ui.screens.timetable.TimetableViewModel

@Composable
inline fun <reified T : ViewModel> getViewModel(
    crossinline creator: (app: LumeraApplication) -> T
): T {
    val context = LocalContext.current.applicationContext as LumeraApplication
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return creator(context) as VM
            }
        }
    )
}
