/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alzimerahmed.attendsmartly.AttendSmartlyApplication
import com.alzimerahmed.attendsmartly.ui.screens.analytics.AnalyticsViewModel
import com.alzimerahmed.attendsmartly.ui.screens.history.HistoryViewModel
import com.alzimerahmed.attendsmartly.ui.screens.home.HomeViewModel
import com.alzimerahmed.attendsmartly.ui.screens.settings.SettingsViewModel
import com.alzimerahmed.attendsmartly.ui.screens.subjectdetails.SubjectDetailViewModel
import com.alzimerahmed.attendsmartly.ui.screens.subjects.SubjectsViewModel
import com.alzimerahmed.attendsmartly.ui.screens.timetable.TimetableViewModel

@Composable
inline fun <reified T : ViewModel> getViewModel(
    crossinline creator: (app: AttendSmartlyApplication) -> T
): T {
    val context = LocalContext.current.applicationContext as AttendSmartlyApplication
    return viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return creator(context) as VM
            }
        }
    )
}
