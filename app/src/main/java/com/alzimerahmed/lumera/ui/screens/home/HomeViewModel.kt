/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.alzimerahmed.lumera.data.local.entity.AttendanceSessionEntity
import com.alzimerahmed.lumera.data.local.entity.AttendanceUnitEntity
import com.alzimerahmed.lumera.data.local.entity.SubjectEntity
import com.alzimerahmed.lumera.data.local.entity.TimetableEntryEntity
import com.alzimerahmed.lumera.data.preferences.UserPreferences
import com.alzimerahmed.lumera.data.preferences.UserPreferencesRepository
import com.alzimerahmed.lumera.data.repository.LumeraRepository
import com.alzimerahmed.lumera.domain.calculator.AttendanceCalculator
import com.alzimerahmed.lumera.domain.calculator.AttendanceSummary
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import com.alzimerahmed.lumera.domain.model.ClassScheduleItem
import com.alzimerahmed.lumera.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LumeraRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedDateIso = MutableStateFlow(DateUtils.todayIso())
    val selectedDateIso: StateFlow<String> = _selectedDateIso.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    val allActiveSubjects: StateFlow<List<SubjectEntity>> = repository.activeSubjects
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Last action for Undo
    private var lastSessionIdForUndo: Long? = null
    private var lastUnitsStateForUndo: List<AttendanceUnitEntity>? = null

    val todaySchedules: StateFlow<List<ClassScheduleItem>> = _selectedDateIso
        .flatMapLatest { dateIso ->
            val dayOfWeek = DateUtils.getDayOfWeekInt(dateIso)
            combine(
                repository.getTimetableForDayAndDate(dayOfWeek, dateIso),
                repository.allSubjects,
                repository.getSessionsForDate(dateIso),
                repository.allUnits,
                repository.allHolidays
            ) { timetable, subjects, sessions, allUnits, holidays ->
                val holiday = holidays.find { it.date == dateIso }
                val isHoliday = holiday != null

                // 1. Regular timetable items
                val scheduledItems = timetable.mapNotNull { entry ->
                    val subject = subjects.find { it.id == entry.subjectId } ?: return@mapNotNull null
                    val session = sessions.find {
                        (it.timetableEntryId != null && it.timetableEntryId == entry.id) ||
                        (it.subjectId == entry.subjectId && it.startTime == entry.startTime)
                    }
                    val units = if (session != null) {
                        allUnits.filter { it.sessionId == session.id }
                    } else {
                        emptyList()
                    }

                    ClassScheduleItem(
                        session = session,
                        timetableEntry = entry,
                        subject = subject,
                        units = units,
                        isHoliday = isHoliday,
                        holidayTitle = holiday?.title
                    )
                }

                // 2. Extra / Rescheduled sessions that are NOT already in scheduledItems
                val matchedSessionIds = scheduledItems.mapNotNull { it.session?.id }.toSet()
                val standaloneSessions = sessions.filter { session ->
                    !matchedSessionIds.contains(session.id) &&
                    (session.isRescheduled || allUnits.any { it.sessionId == session.id && it.status != AttendanceStatus.UNMARKED.name })
                }.mapNotNull { session ->
                    val subject = subjects.find { it.id == session.subjectId } ?: return@mapNotNull null
                    val units = allUnits.filter { it.sessionId == session.id }
                    val syntheticTimetableEntry = TimetableEntryEntity(
                        id = -session.id,
                        subjectId = session.subjectId,
                        dayOfWeek = dayOfWeek,
                        startTime = session.startTime,
                        endTime = session.endTime,
                        roomOverride = subject.room,
                        teacherOverride = subject.teacherName,
                        attendanceUnitCount = session.expectedUnitCount
                    )

                    ClassScheduleItem(
                        session = session,
                        timetableEntry = syntheticTimetableEntry,
                        subject = subject,
                        units = units,
                        isHoliday = isHoliday,
                        holidayTitle = holiday?.title
                    )
                }

                (scheduledItems + standaloneSessions).sortedBy { it.timetableEntry.startTime }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Ticks every 30s to keep the live "current class" card fresh. */
    private val nowTicker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            kotlinx.coroutines.delay(30_000L)
        }
    }

    data class CurrentClassInfo(
        val item: ClassScheduleItem,
        val minutesRemaining: Long,
        val endsAtDisplay: String
    )

    val currentClass: StateFlow<CurrentClassInfo?> = combine(todaySchedules, nowTicker) { schedules, _ ->
        if (_selectedDateIso.value != DateUtils.todayIso()) return@combine null
        val now = java.time.LocalTime.now()
        val active = schedules.firstOrNull { item ->
            try {
                val start = java.time.LocalTime.parse(item.timetableEntry.startTime, DateUtils.timeFormatter24)
                val end = java.time.LocalTime.parse(item.timetableEntry.endTime, DateUtils.timeFormatter24)
                !now.isBefore(start) && now.isBefore(end)
            } catch (_: Exception) {
                false
            }
        } ?: return@combine null
        try {
            val end = java.time.LocalTime.parse(active.timetableEntry.endTime, DateUtils.timeFormatter24)
            val remaining = java.time.Duration.between(now, end).toMinutes() + 1
            CurrentClassInfo(
                item = active,
                minutesRemaining = remaining,
                endsAtDisplay = DateUtils.formatTime(active.timetableEntry.endTime)
            )
        } catch (_: Exception) {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isSelectedDateInSemester: StateFlow<Boolean> = combine(
        _selectedDateIso,
        userPreferences
    ) { dateIso, prefs ->
        isDateInSemester(dateIso, prefs)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    data class SubjectRisk(
        val subjectId: Long,
        val name: String,
        val colorValue: Long,
        val percentage: Double,
        val target: Double,
        val safeBunks: Int,
        val requiredUnits: Int
    ) {
        val isRecovering: Boolean get() = percentage < target - 1e-9
        val isAtRisk: Boolean get() = !isRecovering && percentage < target + 5.0
    }

    /** Per-subject risk list, most at-risk first. */
    val subjectRisks: StateFlow<List<SubjectRisk>> = combine(
        repository.allUnits,
        repository.allSessions,
        repository.activeSubjects,
        userPreferences
    ) { units, sessions, subjects, _ ->
        subjects.map { subject ->
            val sessionIds = sessions.filter { it.subjectId == subject.id }.map { it.id }.toSet()
            val statuses = units.filter { it.sessionId in sessionIds }.mapNotNull {
                try { AttendanceStatus.valueOf(it.status) } catch (_: Exception) { null }
            }
            val summary = AttendanceCalculator.calculate(statuses, subject.targetPercentage)
            SubjectRisk(
                subjectId = subject.id,
                name = subject.name,
                colorValue = subject.colorValue,
                percentage = summary.percentage,
                target = subject.targetPercentage,
                safeBunks = summary.safeBunks,
                requiredUnits = summary.requiredUnitsToTarget
            )
        }.sortedBy { it.percentage - it.target }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val overallSummary: StateFlow<AttendanceSummary> = combine(
        repository.allUnits,
        repository.allSessions,
        userPreferences
    ) { units, sessions, prefs ->
        val validUnits = if (prefs.trackBySemester && (prefs.semesterStartDate.isNotBlank() || prefs.semesterEndDate.isNotBlank())) {
            val sessionDateMap = sessions.associate { it.id to it.sessionDate }
            units.filter { unit ->
                val sDate = sessionDateMap[unit.sessionId] ?: ""
                val afterStart = prefs.semesterStartDate.isBlank() || sDate >= prefs.semesterStartDate
                val beforeEnd = prefs.semesterEndDate.isBlank() || sDate <= prefs.semesterEndDate
                afterStart && beforeEnd
            }
        } else {
            units
        }
        val statuses = validUnits.mapNotNull {
            try { AttendanceStatus.valueOf(it.status) } catch (e: Exception) { null }
        }
        AttendanceCalculator.calculate(statuses, prefs.defaultTargetAttendance)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AttendanceCalculator.calculate(emptyList()))

    fun isDateInSemester(dateIso: String, prefs: UserPreferences = userPreferences.value): Boolean {
        if (!prefs.trackBySemester) return true
        if (prefs.semesterStartDate.isNotBlank() && dateIso < prefs.semesterStartDate) return false
        if (prefs.semesterEndDate.isNotBlank() && dateIso > prefs.semesterEndDate) return false
        return true
    }

    fun selectDate(dateIso: String) {
        _selectedDateIso.value = dateIso
    }

    private suspend fun getOrCreateSession(item: ClassScheduleItem): Long {
        if (item.session != null) return item.session.id

        val newSession = AttendanceSessionEntity(
            subjectId = item.subject.id,
            timetableEntryId = if (item.timetableEntry.id > 0) item.timetableEntry.id else null,
            sessionDate = _selectedDateIso.value,
            startTime = item.timetableEntry.startTime,
            endTime = item.timetableEntry.endTime,
            expectedUnitCount = item.timetableEntry.attendanceUnitCount
        )

        val units = (0 until item.timetableEntry.attendanceUnitCount).map { index ->
            AttendanceUnitEntity(
                sessionId = 0,
                unitIndex = index,
                status = AttendanceStatus.UNMARKED.name
            )
        }

        return repository.createOrUpdateSessionWithUnits(newSession, units)
    }

    fun markPresent(item: ClassScheduleItem) {
        viewModelScope.launch {
            val sessionId = getOrCreateSession(item)
            saveUndoState(sessionId)
            repository.markOneUnitStatus(sessionId, AttendanceStatus.PRESENT)
        }
    }

    fun markAbsent(item: ClassScheduleItem) {
        viewModelScope.launch {
            val sessionId = getOrCreateSession(item)
            saveUndoState(sessionId)
            repository.markOneUnitStatus(sessionId, AttendanceStatus.ABSENT)
        }
    }

    fun markBunked(item: ClassScheduleItem) {
        viewModelScope.launch {
            val sessionId = getOrCreateSession(item)
            saveUndoState(sessionId)
            repository.markOneUnitStatus(sessionId, AttendanceStatus.BUNKED)
        }
    }

    fun markCancelled(item: ClassScheduleItem) {
        viewModelScope.launch {
            val sessionId = getOrCreateSession(item)
            saveUndoState(sessionId)
            repository.markOneUnitStatus(sessionId, AttendanceStatus.CANCELLED)
        }
    }

    fun updateUnitStatus(sessionId: Long, unitId: Long, newStatus: AttendanceStatus) {
        viewModelScope.launch {
            saveUndoState(sessionId)
            repository.updateUnitStatus(unitId, newStatus)
        }
    }

    fun markAllSessionStatus(item: ClassScheduleItem, status: AttendanceStatus) {
        viewModelScope.launch {
            val sessionId = getOrCreateSession(item)
            saveUndoState(sessionId)
            repository.markCompleteSessionStatus(sessionId, status)
        }
    }

    fun resetSession(item: ClassScheduleItem) {
        viewModelScope.launch {
            val sessionId = getOrCreateSession(item)
            saveUndoState(sessionId)
            repository.resetSessionAttendance(sessionId)
        }
    }

    fun rescheduleClass(
        item: ClassScheduleItem,
        newDate: String,
        newStartTime: String,
        newEndTime: String,
        unitCount: Int,
        reason: String = ""
    ) {
        viewModelScope.launch {
            repository.rescheduleClassSession(
                originalDate = _selectedDateIso.value,
                timetableEntryId = if (item.timetableEntry.id > 0) item.timetableEntry.id else null,
                subjectId = item.subject.id,
                originalTime = item.timetableEntry.startTime,
                newDate = newDate,
                newStartTime = newStartTime,
                newEndTime = newEndTime,
                unitCount = unitCount,
                reason = reason
            )
        }
    }

    fun rescheduleSubjectClass(
        subjectId: Long,
        originalDate: String,
        originalTime: String,
        newDate: String,
        newStartTime: String,
        newEndTime: String,
        unitCount: Int,
        reason: String = ""
    ) {
        viewModelScope.launch {
            repository.rescheduleClassSession(
                originalDate = originalDate,
                timetableEntryId = null,
                subjectId = subjectId,
                originalTime = originalTime,
                newDate = newDate,
                newStartTime = newStartTime,
                newEndTime = newEndTime,
                unitCount = unitCount,
                reason = reason
            )
        }
    }

    fun addExtraClass(
        subjectId: Long,
        dateIso: String,
        startTime: String,
        endTime: String,
        unitCount: Int,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addExtraClassSession(
                subjectId = subjectId,
                dateIso = dateIso,
                startTime = startTime,
                endTime = endTime,
                unitCount = unitCount,
                notes = notes
            )
        }
    }

    fun cancelReschedule(item: ClassScheduleItem) {
        viewModelScope.launch {
            val session = item.session ?: if (item.timetableEntry.id > 0) {
                repository.getSessionsForDate(_selectedDateIso.value).first().find { it.timetableEntryId == item.timetableEntry.id }
            } else null

            if (session != null) {
                repository.cancelReschedule(session)
            }
        }
    }

    private suspend fun saveUndoState(sessionId: Long) {
        lastSessionIdForUndo = sessionId
        lastUnitsStateForUndo = repository.getUnitsForSession(sessionId).first()
    }

    fun undoLastAction() {
        val sessionId = lastSessionIdForUndo ?: return
        val previousUnits = lastUnitsStateForUndo ?: return
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId) ?: return@launch
            repository.createOrUpdateSessionWithUnits(session, previousUnits)
            lastSessionIdForUndo = null
            lastUnitsStateForUndo = null
        }
    }
}