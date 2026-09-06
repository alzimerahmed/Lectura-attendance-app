/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.alzimerahmed.lumera.data.local.database.AppDatabase
import com.alzimerahmed.lumera.data.preferences.UserPreferencesRepository
import com.alzimerahmed.lumera.data.repository.LumeraRepository
import com.alzimerahmed.lumera.notification.NotificationHelper
import com.alzimerahmed.lumera.worker.ReminderWorker

@HiltAndroidApp
class LumeraApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: LumeraRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        database = AppDatabase.getInstance(this)
        userPreferencesRepository = UserPreferencesRepository(this)
        
        repository = LumeraRepository(
            subjectDao = database.subjectDao(),
            timetableDao = database.timetableDao(),
            attendanceDao = database.attendanceDao(),
            holidayDao = database.holidayDao(),
            assignmentDao = database.assignmentDao(),
            examDao = database.examDao()
        )

        NotificationHelper.createNotificationChannel(this)
        ReminderWorker.schedulePeriodicReminderCheck(this)
        com.alzimerahmed.lumera.widget.LumeraWidgetReceiver.scheduleWidgetRefresh(this)
        com.alzimerahmed.lumera.worker.AutoBackupWorker.scheduleAutoBackup(this)
    }
}
