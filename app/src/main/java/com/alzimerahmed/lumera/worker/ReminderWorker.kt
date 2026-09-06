/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.worker

import android.content.Context
import androidx.work.*
import com.alzimerahmed.lumera.LumeraApplication
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import com.alzimerahmed.lumera.notification.NotificationHelper
import com.alzimerahmed.lumera.util.DateUtils
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val app = context.applicationContext as LumeraApplication
            val repository = app.repository
            val prefsRepo = app.userPreferencesRepository
            val userPrefs = prefsRepo.userPreferencesFlow.first()

            if (!userPrefs.notificationsEnabled) {
                return Result.success()
            }

            val todayIso = DateUtils.todayIso()

            if (userPrefs.trackBySemester) {
                if (userPrefs.semesterStartDate.isNotBlank() && todayIso < userPrefs.semesterStartDate) {
                    return Result.success()
                }
                if (userPrefs.semesterEndDate.isNotBlank() && todayIso > userPrefs.semesterEndDate) {
                    return Result.success()
                }
            }

            val dayOfWeek = DateUtils.getDayOfWeekInt(todayIso)

            val activeTimetable = repository.getTimetableForDay(dayOfWeek, todayIso).first()
            val now = LocalTime.now()
            val today = LocalDate.now()

            for (entry in activeTimetable) {
                val subject = repository.getSubjectById(entry.subjectId) ?: continue
                if (subject.isArchived) continue

                val reminderMins = if (entry.reminderMinutes > 0) {
                    entry.reminderMinutes
                } else if (subject.defaultReminderMinutes > 0) {
                    subject.defaultReminderMinutes
                } else if (userPrefs.defaultReminderMinutes > 0) {
                    userPrefs.defaultReminderMinutes
                } else {
                    10
                }

                if (reminderMins <= 0) continue

                val classStartTime = LocalTime.parse(entry.startTime, DateUtils.timeFormatter24)
                val reminderTime = classStartTime.minusMinutes(reminderMins.toLong())
                val durationMins = DateUtils.calculateDurationMinutes(entry.startTime, entry.endTime)
                val room = if (entry.roomOverride.isNotBlank()) entry.roomOverride else subject.room
                val teacher = if (entry.teacherOverride.isNotBlank()) entry.teacherOverride else subject.teacherName

                if (now.isBefore(reminderTime)) {
                    // Schedule exact alarm for reminderTime
                    val reminderDateTime = LocalDateTime.of(today, reminderTime)
                    val triggerAtMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

                    NotificationHelper.scheduleAlarm(
                        context = context,
                        triggerAtMillis = triggerAtMillis,
                        sessionId = entry.id,
                        subjectName = subject.name,
                        startTime = DateUtils.formatTime(entry.startTime),
                        room = room,
                        teacher = teacher,
                        durationMinutes = durationMins,
                        unitCount = entry.attendanceUnitCount,
                        minutesBefore = reminderMins
                    )
                } else if (!now.isBefore(reminderTime) && now.isBefore(classStartTime)) {
                    // Fallback: If now is within 0..2 minutes past reminderTime, show notification directly
                    val minutesPastReminder = java.time.Duration.between(reminderTime, now).toMinutes()
                    if (minutesPastReminder in 0..2) {
                        NotificationHelper.showClassReminderNotification(
                            context = context,
                            notificationId = entry.id.toInt(),
                            sessionId = entry.id,
                            subjectName = subject.name,
                            startTime = DateUtils.formatTime(entry.startTime),
                            room = room,
                            teacher = teacher,
                            durationMinutes = durationMins,
                            unitCount = entry.attendanceUnitCount,
                            minutesBefore = reminderMins
                        )
                    }
                }

                // Class-start alarm: persistent in-progress notification with live countdown
                if (now.isBefore(classStartTime)) {
                    val startDateTime = LocalDateTime.of(today, classStartTime)
                    val startMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    NotificationHelper.scheduleAlarm(
                        context = context,
                        triggerAtMillis = startMillis,
                        sessionId = entry.id,
                        subjectName = subject.name,
                        startTime = DateUtils.formatTime(entry.startTime),
                        room = room,
                        teacher = teacher,
                        durationMinutes = durationMins,
                        unitCount = entry.attendanceUnitCount,
                        minutesBefore = 0
                    )
                }

                // Post-class nudge: class ended >= 15 min ago and attendance still unmarked
                val classEndTime = try { LocalTime.parse(entry.endTime, DateUtils.timeFormatter24) } catch (_: Exception) { null }
                if (classEndTime != null && now.isAfter(classEndTime.plusMinutes(15))) {
                    val session = repository.getSessionForTimetableAndDate(entry.id, todayIso)
                    val units = session?.let { repository.getUnitsForSession(it.id).first() } ?: emptyList()
                    val allMarked = units.isNotEmpty() && units.none { it.status == AttendanceStatus.UNMARKED.name }
                    if (session != null && !allMarked) {
                        NotificationHelper.showUnmarkedNudge(
                            context = context,
                            notificationId = (entry.id + 5000).toInt(),
                            sessionId = entry.id,
                            subjectName = subject.name,
                            endTime = DateUtils.formatTime(entry.endTime)
                        )
                    }
                }
            }
            // Assignment due-soon notifications (due within 24h, not done)
            val tomorrowIso = LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
            val dueSoon = app.repository.allAssignments.first()
                .filter { !it.isDone && it.dueDateIso >= todayIso && it.dueDateIso <= tomorrowIso }
            for (assignment in dueSoon) {
                val subject = repository.getSubjectById(assignment.subjectId)
                NotificationHelper.showAssignmentDueNotification(
                    context = context,
                    assignmentId = assignment.id,
                    subjectName = subject?.name ?: "",
                    title = assignment.title,
                    dueDateIso = assignment.dueDateIso
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        private const val WORK_NAME = "Lumera_reminder_work"

        fun schedulePeriodicReminderCheck(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}