/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alzimerahmed.lumera.LumeraApplication
import com.alzimerahmed.lumera.data.local.entity.AttendanceSessionEntity
import com.alzimerahmed.lumera.data.local.entity.AttendanceUnitEntity
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import com.alzimerahmed.lumera.notification.NotificationHelper
import com.alzimerahmed.lumera.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReminderNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val timetableEntryId = intent.getLongExtra(EXTRA_SESSION_ID, -1L)
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, timetableEntryId.toInt())

        val app = context.applicationContext as LumeraApplication
        val repository = app.repository
        val userPrefsRepo = app.userPreferencesRepository

        when (action) {
            ACTION_MARK_PRESENT -> {
                if (timetableEntryId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val prefs = userPrefsRepo.userPreferencesFlow.first()
                            if (prefs.notificationsEnabled) {
                                val todayIso = DateUtils.todayIso()
                                var session = repository.getSessionForTimetableAndDate(timetableEntryId, todayIso)
                                var sessionId = session?.id ?: -1L

                                if (sessionId == -1L) {
                                    val entry = repository.getTimetableEntryById(timetableEntryId)
                                    if (entry != null) {
                                        val newSession = AttendanceSessionEntity(
                                            subjectId = entry.subjectId,
                                            timetableEntryId = entry.id,
                                            sessionDate = todayIso,
                                            startTime = entry.startTime,
                                            endTime = entry.endTime,
                                            expectedUnitCount = entry.attendanceUnitCount
                                        )
                                        val units = (0 until entry.attendanceUnitCount).map { idx ->
                                            AttendanceUnitEntity(
                                                sessionId = 0,
                                                unitIndex = idx,
                                                status = AttendanceStatus.UNMARKED.name
                                            )
                                        }
                                        sessionId = repository.createOrUpdateSessionWithUnits(newSession, units)
                                    }
                                }

                                if (sessionId != -1L) {
                                    repository.markOneUnitStatus(sessionId, AttendanceStatus.PRESENT)
                                }
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
                cancelNotification(context, notificationId)
            }
            ACTION_MARK_ABSENT -> {
                if (timetableEntryId != -1L) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val prefs = userPrefsRepo.userPreferencesFlow.first()
                            if (prefs.notificationsEnabled) {
                                val todayIso = DateUtils.todayIso()
                                var session = repository.getSessionForTimetableAndDate(timetableEntryId, todayIso)
                                var sessionId = session?.id ?: -1L

                                if (sessionId == -1L) {
                                    val entry = repository.getTimetableEntryById(timetableEntryId)
                                    if (entry != null) {
                                        val newSession = AttendanceSessionEntity(
                                            subjectId = entry.subjectId,
                                            timetableEntryId = entry.id,
                                            sessionDate = todayIso,
                                            startTime = entry.startTime,
                                            endTime = entry.endTime,
                                            expectedUnitCount = entry.attendanceUnitCount
                                        )
                                        val units = (0 until entry.attendanceUnitCount).map { idx ->
                                            AttendanceUnitEntity(
                                                sessionId = 0,
                                                unitIndex = idx,
                                                status = AttendanceStatus.UNMARKED.name
                                            )
                                        }
                                        sessionId = repository.createOrUpdateSessionWithUnits(newSession, units)
                                    }
                                }

                                if (sessionId != -1L) {
                                    repository.markOneUnitStatus(sessionId, AttendanceStatus.ABSENT)
                                }
                            }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                }
                cancelNotification(context, notificationId)
            }
            ACTION_TRIGGER_REMINDER -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val prefs = userPrefsRepo.userPreferencesFlow.first()
                        if (prefs.notificationsEnabled) {
                            val subjectName = intent.getStringExtra(EXTRA_SUBJECT_NAME) ?: "Class"
                            val startTime = intent.getStringExtra(EXTRA_START_TIME) ?: ""
                            val room = intent.getStringExtra(EXTRA_ROOM) ?: ""
                            val teacher = intent.getStringExtra(EXTRA_TEACHER) ?: ""
                            val durationMinutes = intent.getIntExtra(EXTRA_DURATION, 60)
                            val unitCount = intent.getIntExtra(EXTRA_UNIT_COUNT, 1)
                            val minutesBefore = intent.getIntExtra(EXTRA_MINUTES_BEFORE, 10)

                            if (minutesBefore <= 0) {
                                // Class-start alarm: persistent in-progress notification with live chronometer
                                val classStartMillis = System.currentTimeMillis()
                                NotificationHelper.showOngoingClassNotification(
                                    context = context,
                                    notificationId = notificationId,
                                    sessionId = timetableEntryId,
                                    subjectName = subjectName,
                                    startTime = startTime,
                                    room = room,
                                    teacher = teacher,
                                    durationMinutes = durationMinutes,
                                    unitCount = unitCount,
                                    classStartMillis = classStartMillis
                                )
                            } else {
                                NotificationHelper.showClassReminderNotification(
                                    context = context,
                                    notificationId = notificationId,
                                    sessionId = timetableEntryId,
                                    subjectName = subjectName,
                                    startTime = startTime,
                                    room = room,
                                    teacher = teacher,
                                    durationMinutes = durationMinutes,
                                    unitCount = unitCount,
                                    minutesBefore = minutesBefore
                                )
                            }
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private fun cancelNotification(context: Context, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)
    }

    companion object {
        const val ACTION_MARK_PRESENT = "com.alzimerahmed.lumera.ACTION_MARK_PRESENT"
        const val ACTION_MARK_ABSENT = "com.alzimerahmed.lumera.ACTION_MARK_ABSENT"
        const val ACTION_TRIGGER_REMINDER = "com.alzimerahmed.lumera.ACTION_TRIGGER_REMINDER"

        const val EXTRA_SESSION_ID = "extra_session_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_SUBJECT_NAME = "extra_subject_name"
        const val EXTRA_START_TIME = "extra_start_time"
        const val EXTRA_ROOM = "extra_room"
        const val EXTRA_TEACHER = "extra_teacher"
        const val EXTRA_DURATION = "extra_duration"
        const val EXTRA_UNIT_COUNT = "extra_unit_count"
        const val EXTRA_MINUTES_BEFORE = "extra_minutes_before"
    }
}
