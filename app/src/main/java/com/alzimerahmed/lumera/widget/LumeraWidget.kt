/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.alzimerahmed.lumera.data.local.database.AppDatabase
import com.alzimerahmed.lumera.data.preferences.UserPreferencesRepository
import com.alzimerahmed.lumera.domain.calculator.AttendanceCalculator
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import com.alzimerahmed.lumera.util.DateUtils
import kotlinx.coroutines.flow.first
import java.time.LocalTime

data class WidgetData(
    val overallPercent: Int,
    val safeBunks: Int,
    val nextClassName: String,
    val nextClassTime: String
)

object WidgetDataLoader {
    suspend fun load(context: Context): WidgetData {
        val db = AppDatabase.getInstance(context)
        val prefsRepo = UserPreferencesRepository(context)
        val prefs = prefsRepo.userPreferencesFlow.first()

        val sessions = db.attendanceDao().getAllSessions().first()
        val units = db.attendanceDao().getAllUnits().first()
        val subjects = db.subjectDao().getActiveSubjects().first()

        val statuses = units.mapNotNull { unit ->
            try { AttendanceStatus.valueOf(unit.status) } catch (_: Exception) { null }
        }
        val summary = AttendanceCalculator.calculate(statuses, prefs.defaultTargetAttendance)
        val overallPercent = summary.percentage.toInt()

        // Next upcoming class today
        val todayIso = DateUtils.todayIso()
        val dayOfWeek = DateUtils.getDayOfWeekInt(todayIso)
        val entries = db.timetableDao().getEntriesForDay(dayOfWeek, todayIso).first()
        val now = LocalTime.now()
        val next = entries
            .mapNotNull { entry ->
                val subject = subjects.find { it.id == entry.subjectId } ?: return@mapNotNull null
                try {
                    val start = LocalTime.parse(entry.startTime, DateUtils.timeFormatter24)
                    if (start.isAfter(now)) entry to subject else null
                } catch (_: Exception) { null }
            }
            .minByOrNull { it.first.startTime }

        return WidgetData(
            overallPercent = overallPercent,
            safeBunks = summary.safeBunks,
            nextClassName = next?.second?.name ?: "No more classes today",
            nextClassTime = next?.let { DateUtils.formatTime(it.first.startTime) } ?: ""
        )
    }
}

class LumeraWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = WidgetDataLoader.load(context)
        provideContent {
            GlanceTheme {
                WidgetContent(data)
            }
        }
    }
}

@Composable
private fun WidgetContent(data: WidgetData) {
    Box(
        modifier = GlanceModifier.fillMaxSize().padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${data.overallPercent}%",
                style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold),
                modifier = GlanceModifier.padding(top = 8.dp)
            )
            Text(
                text = "Attendance",
                style = TextStyle(fontSize = 12.sp)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = GlanceModifier.background(ColorProvider(Color(0xFF4CAF50))).width(8.dp).height(8.dp)
                ) {}
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "Safe bunks: ${data.safeBunks}",
                    style = TextStyle(fontSize = 12.sp)
                )
            }
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                text = "Next: ${data.nextClassName}",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                modifier = GlanceModifier.padding(horizontal = 4.dp)
            )
            if (data.nextClassTime.isNotBlank()) {
                Text(
                    text = "at ${data.nextClassTime}",
                    style = TextStyle(fontSize = 11.sp)
                )
            }
        }
    }
}
