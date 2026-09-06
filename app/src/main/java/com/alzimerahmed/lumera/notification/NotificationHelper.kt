/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.alzimerahmed.lumera.MainActivity
import com.alzimerahmed.lumera.R
import com.alzimerahmed.lumera.receiver.ReminderNotificationReceiver

object NotificationHelper {

    const val CHANNEL_ID = "Lumera_class_reminders"
    const val CHANNEL_NAME = "Class Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming college classes"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showClassReminderNotification(
        context: Context,
        notificationId: Int,
        sessionId: Long,
        subjectName: String,
        startTime: String,
        room: String,
        teacher: String,
        durationMinutes: Int,
        unitCount: Int,
        minutesBefore: Int
    ) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Present action
        val presentIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_MARK_PRESENT
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val presentPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            presentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Absent action
        val absentIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_MARK_ABSENT
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val absentPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            absentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val locationInfo = if (room.isNotBlank()) " in $room" else ""
        val teacherInfo = if (teacher.isNotBlank()) " with $teacher" else ""
        val timeMessage = if (minutesBefore > 0) "starts in $minutesBefore mins at $startTime" else "starts now at $startTime"
        
        val contentText = "$subjectName$locationInfo$teacherInfo ($durationMinutes mins • $unitCount ${if (unitCount == 1) "unit" else "units"}) $timeMessage."

        val appIconLarge = try {
            BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        } catch (_: Exception) {
            null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .apply {
                if (appIconLarge != null) {
                    setLargeIcon(appIconLarge)
                }
            }
            .setContentTitle("Upcoming Class: $subjectName")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(0, "Mark Present", presentPendingIntent)
            .addAction(0, "Mark Absent", absentPendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }

    /** Notification for an assignment due within 24 hours. */
    fun showAssignmentDueNotification(context: Context, assignmentId: Long, subjectName: String, title: String, dueDateIso: String) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            (assignmentId + 9000).toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = "$subjectName: $title is due ${dueDateIso}. Don't forget to submit it."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Assignment Due Soon")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((assignmentId + 9000).toInt(), builder.build())
    }

    /** Ongoing notification shown while a class is in progress. Stays until the user
     * marks attendance (actions auto-cancel it) — ClassTrack-style persistent marking.
     */
    fun showOngoingClassNotification(
        context: Context,
        notificationId: Int,
        sessionId: Long,
        subjectName: String,
        startTime: String,
        room: String,
        teacher: String,
        durationMinutes: Int,
        unitCount: Int,
        classStartMillis: Long
    ) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val presentIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_MARK_PRESENT
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val presentPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            presentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val absentIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_MARK_ABSENT
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val absentPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            absentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val locationInfo = if (room.isNotBlank()) " in $room" else ""
        val contentText = "$subjectName$locationInfo is in progress. Mark your attendance before it ends."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Class Now: $subjectName")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setWhen(classStartMillis)
            .setUsesChronometer(true)
            .setContentIntent(openAppPendingIntent)
            .addAction(0, "Present", presentPendingIntent)
            .addAction(0, "Absent", absentPendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }

    /** Nudge shown after a class ended but its attendance was never marked. */
    fun showUnmarkedNudge(
        context: Context,
        notificationId: Int,
        sessionId: Long,
        subjectName: String,
        endTime: String
    ) {
        createNotificationChannel(context)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val presentIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_MARK_PRESENT
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val presentPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 1,
            presentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val absentIntent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_MARK_ABSENT
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val absentPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId * 10 + 2,
            absentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = "$subjectName ended at $endTime. Did you attend? Mark it before you forget."

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Unmarked Attendance")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openAppPendingIntent)
            .addAction(0, "Present", presentPendingIntent)
            .addAction(0, "Absent", absentPendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }

    fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        sessionId: Long,
        subjectName: String,
        startTime: String,
        room: String = "",
        teacher: String = "",
        durationMinutes: Int = 60,
        unitCount: Int = 1,
        minutesBefore: Int = 10
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ReminderNotificationReceiver.ACTION_TRIGGER_REMINDER
            putExtra(ReminderNotificationReceiver.EXTRA_SESSION_ID, sessionId)
            putExtra(ReminderNotificationReceiver.EXTRA_SUBJECT_NAME, subjectName)
            putExtra(ReminderNotificationReceiver.EXTRA_START_TIME, startTime)
            putExtra(ReminderNotificationReceiver.EXTRA_ROOM, room)
            putExtra(ReminderNotificationReceiver.EXTRA_TEACHER, teacher)
            putExtra(ReminderNotificationReceiver.EXTRA_DURATION, durationMinutes)
            putExtra(ReminderNotificationReceiver.EXTRA_UNIT_COUNT, unitCount)
            putExtra(ReminderNotificationReceiver.EXTRA_MINUTES_BEFORE, minutesBefore)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (sessionId * 10 + if (minutesBefore <= 0) 1 else 0).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
