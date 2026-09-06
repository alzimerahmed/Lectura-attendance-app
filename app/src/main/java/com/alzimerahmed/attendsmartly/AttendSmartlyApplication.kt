/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly

import android.app.Application
import com.alzimerahmed.attendsmartly.data.local.database.AppDatabase
import com.alzimerahmed.attendsmartly.data.preferences.UserPreferencesRepository
import com.alzimerahmed.attendsmartly.data.repository.AttendSmartlyRepository
import com.alzimerahmed.attendsmartly.notification.NotificationHelper
import com.alzimerahmed.attendsmartly.worker.ReminderWorker

class AttendSmartlyApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: AttendSmartlyRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        database = AppDatabase.getInstance(this)
        userPreferencesRepository = UserPreferencesRepository(this)
        
        repository = AttendSmartlyRepository(
            subjectDao = database.subjectDao(),
            timetableDao = database.timetableDao(),
            attendanceDao = database.attendanceDao(),
            holidayDao = database.holidayDao()
        )

        NotificationHelper.createNotificationChannel(this)
        ReminderWorker.schedulePeriodicReminderCheck(this)
    }
}
