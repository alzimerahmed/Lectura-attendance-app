/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.data.remote.gemini

import android.graphics.Bitmap
import android.graphics.Rect
import com.alzimerahmed.lumera.domain.model.ParsedTimetableItem
import com.alzimerahmed.lumera.util.DateUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

/**
 * On-device timetable OCR fallback using ML Kit text recognition (free, no API key).
 * Parses grid-style timetables: day headers define columns, time labels define rows,
 * and remaining text blocks are assigned to (day, time) cells.
 */
object OnDeviceTimetableOcr {

    private val dayTokens = mapOf(
        "mon" to 1, "monday" to 1,
        "tue" to 2, "tues" to 2, "tuesday" to 2,
        "wed" to 3, "weds" to 3, "wednesday" to 3,
        "thu" to 4, "thur" to 4, "thurs" to 4, "thursday" to 4,
        "fri" to 5, "friday" to 5,
        "sat" to 6, "saturday" to 6,
        "sun" to 7, "sunday" to 7
    )

    private val timeRegex = Regex("(\\d{1,2})[:.](\\d{2})")
    private val junkRegex = Regex("^(timetable|schedule|class|room|time|day|period|subject|faculty|teacher|sem|semester|section|dept|department)$", RegexOption.IGNORE_CASE)

    suspend fun extract(bitmap: Bitmap): List<ParsedTimetableItem> = withContext(Dispatchers.IO) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val visionText = try {
            com.google.android.gms.tasks.Tasks.await(recognizer.process(image))
        } finally {
            recognizer.close()
        }
        parseGrid(visionText)
    }

    internal fun parseGrid(visionText: Text): List<ParsedTimetableItem> {
        data class CellKey(val day: Int, val startMinutes: Int)

        // 1. Locate day-header columns from the line containing the most day tokens
        data class DayColumn(val day: Int, val centerX: Float)

        val dayColumns = mutableListOf<DayColumn>()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val box = line.boundingBox ?: continue
                val tokens = line.text.lowercase().split(Regex("[^a-z]+")).filter { it.isNotBlank() }
                val days = tokens.mapNotNull { dayTokens[it] }.distinct()
                if (days.size >= 2) {
                    // Approximate each day column by dividing the line width evenly
                    val perDay = box.width().toFloat() / days.size
                    days.forEachIndexed { index, day ->
                        dayColumns.add(DayColumn(day, box.left + perDay * (index + 0.5f)))
                    }
                }
            }
        }
        if (dayColumns.isEmpty()) return emptyList()

        // Deduplicate columns per day (keep the topmost occurrence)
        val columns = dayColumns.groupBy { it.day }
            .map { (_, cols) -> cols.minByOrNull { it.centerX }!! }

        // 2. Collect time anchors (row boundaries) from all text
        data class TimeAnchor(val minutes: Int, val centerY: Float)

        val timeAnchors = mutableListOf<TimeAnchor>()
        val textElements = mutableListOf<Triple<String, Rect, String>>() // text, box, lowercased

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val box = line.boundingBox ?: continue
                val lower = line.text.lowercase()
                val isDayHeader = dayColumns.any { line.text.lowercase().contains(dayName(it.day)) } && daysIn(line.text) >= 2
                if (isDayHeader) continue

                for (match in timeRegex.findAll(line.text)) {
                    val h = match.groupValues[1].toInt()
                    val m = match.groupValues[2].toInt()
                    if (h in 0..23 && m in 0..59) {
                        val tokenBox = Rect(box).let { it }
                        timeAnchors.add(TimeAnchor(h * 60 + m, tokenBox.exactCenterY().toFloat()))
                    }
                }
                textElements.add(Triple(line.text, box, lower))
            }
        }
        if (timeAnchors.isEmpty()) return emptyList()

        // Merge anchors that are close in time value
        val anchors = timeAnchors.sortedBy { it.minutes }
            .groupBy { it.minutes }
            .map { (_, list) -> TimeAnchor(list.first().minutes, list.map { it.centerY }.average().toFloat()) }
            .sortedBy { it.centerY }

        // 3. Assign each text element to (day column, time row)
        val cells = mutableMapOf<CellKey, MutableList<String>>()
        for ((text, box, lower) in textElements) {
            val trimmed = text.trim()
            if (trimmed.length < 2) continue
            if (junkRegex.matches(trimmed)) continue
            // Skip pure time lines
            if (timeRegex.matches(trimmed.replace(" ", ""))) continue
            // Skip day-header lines
            if (daysIn(trimmed) >= 2) continue

            val centerX = box.exactCenterX().toFloat()
            val centerY = box.exactCenterY().toFloat()

            val day = columns.minByOrNull { kotlin.math.abs(it.centerX - centerX) }?.day ?: continue
            // Nearest anchor at or above the element's center
            val anchor = anchors.filter { it.centerY <= centerY + 20f }.minByOrNull { kotlin.math.abs(it.centerY - centerY) }
                ?: anchors.firstOrNull()
                ?: continue

            val key = CellKey(day, anchor.minutes)
            cells.getOrPut(key) { mutableListOf() }.add(trimmed)
        }

        // 4. Build items
        val items = mutableListOf<ParsedTimetableItem>()
        val sortedAnchors = anchors.sortedBy { it.minutes }
        for ((key, texts) in cells) {
            val subject = texts.joinToString(" ") { it }
                .replace(timeRegex, "")
                .replace(Regex("\\s+"), " ")
                .trim(' ', '-', '|', ',', '.')
            if (subject.isBlank() || subject.length < 2) continue
            if (junkRegex.matches(subject)) continue

            val startTime = String.format("%02d:%02d", key.startMinutes / 60, key.startMinutes % 60)
            val endMinutes = (sortedAnchors.firstOrNull { it.minutes > key.startMinutes }?.minutes)
                ?: (key.startMinutes + 60)
            val endTime = String.format("%02d:%02d", endMinutes / 60, endMinutes % 60)

            items.add(
                ParsedTimetableItem(
                    subjectName = subject.take(40),
                    startTime = startTime,
                    endTime = endTime,
                    dayOfWeek = key.day,
                    isPractical = subject.contains("lab", ignoreCase = true)
                )
            )
        }
        return items.sortedWith(compareBy({ it.dayOfWeek }, { it.startTime }))
    }

    private fun daysIn(text: String): Int =
        text.lowercase().split(Regex("[^a-z]+")).count { dayTokens.containsKey(it) }

    private fun dayName(day: Int): String = when (day) {
        1 -> "mon"; 2 -> "tue"; 3 -> "wed"; 4 -> "thu"; 5 -> "fri"; 6 -> "sat"; else -> "sun"
    }
}
