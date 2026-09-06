/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.domain.model

import java.util.UUID

data class ParsedTimetableItem(
    val id: String = UUID.randomUUID().toString(),
    var subjectName: String,
    var subjectCode: String = "",
    var teacherName: String = "",
    var startTime: String,
    var endTime: String,
    var roomLocation: String = "",
    var dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    var isPractical: Boolean = false,
    var attendanceUnitCount: Int = 1
)

