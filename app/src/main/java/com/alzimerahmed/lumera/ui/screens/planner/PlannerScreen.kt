/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.screens.planner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alzimerahmed.lumera.data.local.entity.AssignmentEntity
import com.alzimerahmed.lumera.data.local.entity.ExamEntity
import com.alzimerahmed.lumera.data.local.entity.SubjectEntity
import com.alzimerahmed.lumera.util.DateUtils
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val assignments by viewModel.assignments.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    var plannerTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Planner") }) },
        floatingActionButton = {
            androidx.compose.material3.ExtendedFloatingActionButton(onClick = { showAddDialog = true }) {
                Text(if (plannerTab == 0) "+ Assignment" else "+ Exam")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.material3.FilterChip(
                    selected = plannerTab == 0,
                    onClick = { plannerTab = 0 },
                    label = { Text("Assignments") }
                )
                androidx.compose.material3.FilterChip(
                    selected = plannerTab == 1,
                    onClick = { plannerTab = 1 },
                    label = { Text("Exams") }
                )
            }

            if (plannerTab == 0) {
                AssignmentList(
                    assignments = assignments,
                    onToggle = viewModel::toggleAssignmentDone,
                    onDelete = viewModel::deleteAssignment
                )
            } else {
                ExamList(exams = exams, onDelete = viewModel::deleteExam)
            }
        }
    }

    if (showAddDialog) {
        AddPlannerItemDialog(
            isExam = plannerTab == 1,
            subjects = subjects,
            onConfirm = { subjectId, title, dateIso, notes ->
                if (plannerTab == 0) {
                    viewModel.addAssignment(subjectId, title, dateIso, notes)
                } else {
                    viewModel.addExam(subjectId, title, dateIso, notes)
                }
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun AssignmentList(
    assignments: List<AssignmentWithSubject>,
    onToggle: (AssignmentEntity) -> Unit,
    onDelete: (AssignmentEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(assignments, key = { it.assignment.id }) { entry ->
            val assignment = entry.assignment
            val overdue = !assignment.isDone && assignment.dueDateIso < DateUtils.todayIso()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (overdue)
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = assignment.isDone, onCheckedChange = { onToggle(assignment) })
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = assignment.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = buildString {
                                append(entry.subject?.name ?: "General")
                                append(" - due ")
                                append(DateUtils.formatDateToHuman(assignment.dueDateIso))
                                if (overdue) append(" (OVERDUE)")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (overdue)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onDelete(assignment) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamList(
    exams: List<ExamWithSubject>,
    onDelete: (com.alzimerahmed.lumera.data.local.entity.ExamEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(exams, key = { it.exam.id }) { entry ->
            val exam = entry.exam
            val daysLeft = try {
                ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(exam.examDateIso, DateUtils.isoDateFormatter))
            } catch (_: Exception) { 0L }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (entry.isEligibilityRisk)
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exam.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${entry.subject?.name ?: "General"} - ${DateUtils.formatDateToHuman(exam.examDateIso)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (daysLeft >= 0) "in ${daysLeft}d" else "done",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { onDelete(exam) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                    if (entry.isEligibilityRisk) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Warning,
                                null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Attendance ${"%.1f".format(entry.attendancePercent)}% is below the eligibility target",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddPlannerItemDialog(
    isExam: Boolean,
    subjects: List<SubjectEntity>,
    onConfirm: (subjectId: Long, title: String, dateIso: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: 0L) }
    var title by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(DateUtils.todayIso()) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isExam) "Add Exam" else "Add Assignment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                androidx.compose.material3.ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {}
                ) {}
                androidx.compose.material3.OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    label = { Text("Date (yyyy-MM-dd)") },
                    modifier = Modifier.fillMaxWidth()
                )
                androidx.compose.material3.OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isExam) "Syllabus (optional)" else "Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Subject: ${subjects.find { it.id == selectedSubjectId }?.name ?: "General"}",
                    style = MaterialTheme.typography.bodySmall
                )
                if (subjects.isNotEmpty()) {
                    androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(subjects) { subject ->
                            androidx.compose.material3.FilterChip(
                                selected = selectedSubjectId == subject.id,
                                onClick = { selectedSubjectId = subject.id },
                                label = { Text(subject.name, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(selectedSubjectId, title.trim(), dateText, notes.trim())
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
