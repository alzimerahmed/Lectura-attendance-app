/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alzimerahmed.lumera.data.local.entity.AssignmentEntity
import com.alzimerahmed.lumera.data.local.entity.ExamEntity
import com.alzimerahmed.lumera.data.local.entity.SubjectEntity
import com.alzimerahmed.lumera.data.repository.LumeraRepository
import com.alzimerahmed.lumera.domain.calculator.AttendanceCalculator
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AssignmentWithSubject(
    val assignment: AssignmentEntity,
    val subject: SubjectEntity?
)

data class ExamWithSubject(
    val exam: ExamEntity,
    val subject: SubjectEntity?,
    val attendancePercent: Double,
    val isEligibilityRisk: Boolean
)

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val repository: LumeraRepository
) : ViewModel() {

    val subjects: StateFlow<List<SubjectEntity>> = repository.activeSubjects
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val assignments: StateFlow<List<AssignmentWithSubject>> = combine(
        repository.allAssignments,
        repository.allSubjects
    ) { assignments, subjects ->
        assignments.map { AssignmentWithSubject(it, subjects.find { s -> s.id == it.subjectId }) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val exams: StateFlow<List<ExamWithSubject>> = combine(
        repository.allExams,
        repository.allSubjects,
        repository.allSessions,
        repository.allUnits
    ) { exams, subjects, sessions, units ->
        exams.map { exam ->
            val subject = subjects.find { it.id == exam.subjectId }
            val sessionIds = sessions.filter { it.subjectId == exam.subjectId }.map { it.id }.toSet()
            val statuses = units.filter { it.sessionId in sessionIds }.mapNotNull {
                try { AttendanceStatus.valueOf(it.status) } catch (_: Exception) { null }
            }
            val summary = AttendanceCalculator.calculate(
                statuses, subject?.targetPercentage ?: 75.0
            )
            ExamWithSubject(
                exam = exam,
                subject = subject,
                attendancePercent = summary.percentage,
                isEligibilityRisk = summary.percentage < (subject?.targetPercentage ?: 75.0)
            )
        }.sortedBy { it.exam.examDateIso }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addAssignment(subjectId: Long, title: String, dueDateIso: String, notes: String) {
        viewModelScope.launch {
            repository.insertAssignment(
                AssignmentEntity(subjectId = subjectId, title = title, dueDateIso = dueDateIso, notes = notes)
            )
        }
    }

    fun toggleAssignmentDone(assignment: AssignmentEntity) {
        viewModelScope.launch {
            repository.updateAssignment(assignment.copy(isDone = !assignment.isDone))
        }
    }

    fun deleteAssignment(assignment: AssignmentEntity) {
        viewModelScope.launch { repository.deleteAssignment(assignment) }
    }

    fun addExam(subjectId: Long, title: String, examDateIso: String, syllabus: String) {
        viewModelScope.launch {
            repository.insertExam(
                ExamEntity(subjectId = subjectId, title = title, examDateIso = examDateIso, syllabus = syllabus)
            )
        }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch { repository.deleteExam(exam) }
    }
}
