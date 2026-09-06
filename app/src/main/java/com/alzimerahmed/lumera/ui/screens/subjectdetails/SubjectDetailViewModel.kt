/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.screens.subjectdetails

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.alzimerahmed.lumera.data.local.entity.SubjectEntity
import com.alzimerahmed.lumera.data.preferences.UserPreferences
import com.alzimerahmed.lumera.data.preferences.UserPreferencesRepository
import com.alzimerahmed.lumera.data.repository.LumeraRepository
import com.alzimerahmed.lumera.domain.calculator.AttendanceCalculator
import com.alzimerahmed.lumera.domain.calculator.AttendanceSummary
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import com.alzimerahmed.lumera.domain.model.SessionWithUnits
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SubjectDetailViewModel @Inject constructor(
    private val repository: LumeraRepository,
    savedStateHandle: SavedStateHandle,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val subjectId: Long = savedStateHandle.get<Long>("subjectId") ?: 0L

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    val subject: StateFlow<SubjectEntity?> = repository.getSubjectByIdFlow(subjectId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val subjectSessions: StateFlow<List<SessionWithUnits>> = combine(
        repository.getSessionsForSubject(subjectId),
        repository.getAllUnitsForSubject(subjectId),
        userPreferences
    ) { sessions, units, prefs ->
        val validSessions = if (prefs.trackBySemester && (prefs.semesterStartDate.isNotBlank() || prefs.semesterEndDate.isNotBlank())) {
            sessions.filter { session ->
                val afterStart = prefs.semesterStartDate.isBlank() || session.sessionDate >= prefs.semesterStartDate
                val beforeEnd = prefs.semesterEndDate.isBlank() || session.sessionDate <= prefs.semesterEndDate
                afterStart && beforeEnd
            }
        } else {
            sessions
        }
        validSessions.map { session ->
            val sessionUnits = units.filter { it.sessionId == session.id }
            SessionWithUnits(session = session, units = sessionUnits)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val summary: StateFlow<AttendanceSummary> = combine(
        subject,
        repository.getSessionsForSubject(subjectId),
        repository.getAllUnitsForSubject(subjectId),
        userPreferences
    ) { sub, sessions, units, prefs ->
        val target = sub?.targetPercentage ?: 75.0
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
        AttendanceCalculator.calculate(statuses, target)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AttendanceCalculator.calculate(emptyList()))

    fun updateUnitStatus(unitId: Long, newStatus: AttendanceStatus) {
        viewModelScope.launch {
            repository.updateUnitStatus(unitId, newStatus)
        }
    }

    fun markAllSessionStatus(sessionId: Long, status: AttendanceStatus) {
        viewModelScope.launch {
            repository.markCompleteSessionStatus(sessionId, status)
        }
    }

    fun resetSession(sessionId: Long) {
        viewModelScope.launch {
            repository.resetSessionAttendance(sessionId)
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    fun markPastAttendance(count: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, message) = repository.markPastAttendanceForSubject(subjectId, count)
            onResult(success, message)
        }
    }
}