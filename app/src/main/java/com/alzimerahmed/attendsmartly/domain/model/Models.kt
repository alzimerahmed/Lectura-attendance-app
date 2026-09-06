/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.domain.model

import com.alzimerahmed.attendsmartly.data.local.entity.AttendanceSessionEntity
import com.alzimerahmed.attendsmartly.data.local.entity.AttendanceUnitEntity
import com.alzimerahmed.attendsmartly.data.local.entity.SubjectEntity
import com.alzimerahmed.attendsmartly.data.local.entity.TimetableEntryEntity

data class SessionWithUnits(
    val session: AttendanceSessionEntity,
    val units: List<AttendanceUnitEntity>
)

data class TimetableWithSubject(
    val entry: TimetableEntryEntity,
    val subject: SubjectEntity
)

data class ClassScheduleItem(
    val session: AttendanceSessionEntity?,
    val timetableEntry: TimetableEntryEntity,
    val subject: SubjectEntity,
    val units: List<AttendanceUnitEntity>,
    val isHoliday: Boolean = false,
    val holidayTitle: String? = null,
    val isRescheduledAway: Boolean = session?.rescheduledToDate != null,
    val rescheduledToDate: String? = session?.rescheduledToDate,
    val rescheduledToTime: String? = session?.rescheduledToTime,
    val isRescheduledIncoming: Boolean = session?.isRescheduled == true || session?.originalDate != null,
    val originalDate: String? = session?.originalDate,
    val originalTime: String? = session?.originalTime,
    val rescheduledReason: String? = session?.rescheduledReason
)
