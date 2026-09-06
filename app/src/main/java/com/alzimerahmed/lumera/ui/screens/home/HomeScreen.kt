/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alzimerahmed.lumera.domain.calculator.AttendanceCalculator
import com.alzimerahmed.lumera.domain.model.ClassScheduleItem
import com.alzimerahmed.lumera.ui.components.AddExtraClassDialog
import com.alzimerahmed.lumera.ui.components.AttendanceProgressCard
import com.alzimerahmed.lumera.ui.components.ClassCard
import com.alzimerahmed.lumera.ui.components.EditUnitBottomSheet
import com.alzimerahmed.lumera.ui.components.RescheduleClassDialog
import com.alzimerahmed.lumera.ui.components.RiskProgressRing
import com.alzimerahmed.lumera.util.DateUtils
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAddSubject: () -> Unit,
    onNavigateToAddTimetable: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val selectedDateIso by viewModel.selectedDateIso.collectAsState()
    val todaySchedules by viewModel.todaySchedules.collectAsState()
    val overallSummary by viewModel.overallSummary.collectAsState()
    val allActiveSubjects by viewModel.allActiveSubjects.collectAsState()
    val currentClass by viewModel.currentClass.collectAsState()
    val subjectRisks by viewModel.subjectRisks.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var activeSheetItem by remember { mutableStateOf<ClassScheduleItem?>(null) }
    var reschedulingItem by remember { mutableStateOf<ClassScheduleItem?>(null) }
    var showGeneralRescheduleDialog by remember { mutableStateOf(false) }
    var showAddExtraClassDialog by remember { mutableStateOf(false) }
    var showFabMenu by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showSimulator by remember { mutableStateOf(false) }

    val userPreferences by viewModel.userPreferences.collectAsState()
    val isSelectedDateInSemester by viewModel.isSelectedDateInSemester.collectAsState()

    val greeting = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 4..11 -> "Good Morning ☀️"
            in 12..16 -> "Good Afternoon 🌤️"
            in 17..22 -> "Good Evening 🌙"
            else -> "Night Owl 🦉"
        }
    }

    val selectedDate = remember(selectedDateIso) {
        try { LocalDate.parse(selectedDateIso, DateUtils.isoDateFormatter) } catch (e: Exception) { LocalDate.now() }
    }

    // Auto-adjust date if outside semester when trackBySemester is enabled
    LaunchedEffect(userPreferences.trackBySemester, userPreferences.semesterStartDate, userPreferences.semesterEndDate) {
        if (userPreferences.trackBySemester && userPreferences.semesterStartDate.isNotBlank() && userPreferences.semesterEndDate.isNotBlank()) {
            try {
                val start = LocalDate.parse(userPreferences.semesterStartDate, DateUtils.isoDateFormatter)
                val end = LocalDate.parse(userPreferences.semesterEndDate, DateUtils.isoDateFormatter)
                val current = LocalDate.parse(selectedDateIso, DateUtils.isoDateFormatter)
                val today = LocalDate.now()
                if (current.isBefore(start)) {
                    val target = if (!today.isBefore(start) && !today.isAfter(end)) today else start
                    viewModel.selectDate(target.format(DateUtils.isoDateFormatter))
                } else if (current.isAfter(end)) {
                    val target = if (!today.isBefore(start) && !today.isAfter(end)) today else end
                    viewModel.selectDate(target.format(DateUtils.isoDateFormatter))
                }
            } catch (_: Exception) {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = greeting,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = DateUtils.formatDateToHuman(selectedDateIso),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDatePickerDialog = true },
                        modifier = Modifier.testTag("date_picker_icon_btn")
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date")
                    }
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("settings_icon_btn")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (showFabMenu) {
                    Column(
                        modifier = Modifier.padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                showAddExtraClassDialog = true
                            },
                            icon = { Icon(Icons.Default.MoreTime, null) },
                            text = { Text("Add Extra Class") }
                        )
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                showGeneralRescheduleDialog = true
                            },
                            icon = { Icon(Icons.Default.EditCalendar, null) },
                            text = { Text("Reschedule Class") }
                        )
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onNavigateToAddSubject()
                            },
                            icon = { Icon(Icons.Default.Book, null) },
                            text = { Text("Add Subject") }
                        )
                        ExtendedFloatingActionButton(
                            onClick = {
                                showFabMenu = false
                                onNavigateToAddTimetable()
                            },
                            icon = { Icon(Icons.Default.CalendarToday, null) },
                            text = { Text("Add Timetable Entry") }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("home_fab_add")
                ) {
                    Icon(
                        imageVector = if (showFabMenu) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Quick Add"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal Date Selector (Bounded by semester if enabled, otherwise relative wide range)
            val dates = remember(selectedDateIso, userPreferences.trackBySemester, userPreferences.semesterStartDate, userPreferences.semesterEndDate) {
                if (userPreferences.trackBySemester && userPreferences.semesterStartDate.isNotBlank() && userPreferences.semesterEndDate.isNotBlank()) {
                    try {
                        val start = LocalDate.parse(userPreferences.semesterStartDate, DateUtils.isoDateFormatter)
                        val end = LocalDate.parse(userPreferences.semesterEndDate, DateUtils.isoDateFormatter)
                        if (!end.isBefore(start)) {
                            val days = java.time.temporal.ChronoUnit.DAYS.between(start, end).toInt()
                            if (days in 0..365) {
                                (0..days).map { start.plusDays(it.toLong()) }
                            } else {
                                val base = selectedDate.minusDays(20)
                                (0..35).map { base.plusDays(it.toLong()) }
                            }
                        } else {
                            val base = selectedDate.minusDays(20)
                            (0..35).map { base.plusDays(it.toLong()) }
                        }
                    } catch (e: Exception) {
                        val base = selectedDate.minusDays(20)
                        (0..35).map { base.plusDays(it.toLong()) }
                    }
                } else {
                    val base = selectedDate.minusDays(20)
                    (0..35).map { base.plusDays(it.toLong()) }
                }
            }
            val dateRowState = rememberLazyListState()

            LaunchedEffect(selectedDateIso, dates) {
                val index = dates.indexOfFirst { it.format(DateUtils.isoDateFormatter) == selectedDateIso }
                if (index >= 0) {
                    dateRowState.animateScrollToItem((index - 2).coerceAtLeast(0))
                }
            }

            LazyRow(
                state = dateRowState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dates) { date ->
                    val dateIso = date.format(DateUtils.isoDateFormatter)
                    val isSelected = dateIso == selectedDateIso
                    val isToday = date == LocalDate.now()

                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val dayNum = date.dayOfMonth

                    Card(
                        modifier = Modifier
                            .testTag("date_chip_$dateIso")
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectDate(dateIso) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                            else if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = dayNum.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Out of semester banner
                if (!isSelectedDateInSemester) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        "Outside Active Semester",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                    Text(
                                        "Semester range: ${userPreferences.semesterStartDate} to ${userPreferences.semesterEndDate}. Attendance marking is paused for this date.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Overall Progress Summary Card - Clean view without Safe Bunk and Goal Status
                item {
                    AttendanceProgressCard(
                        summary = overallSummary,
                        showSafeBunksAndGoal = false
                    )
                }

                // Live "class in progress" hero card
                if (selectedDate == LocalDate.now()) {
                    currentClass?.let { current ->
                        item {
                            CurrentClassCard(info = current)
                        }
                    }
                }

                // Risk strip: subjects below or near their target
                item {
                    val risks = subjectRisks.filter { it.isRecovering || it.isAtRisk }
                    if (risks.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            risks.take(4).forEach { risk ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (risk.isRecovering)
                                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                    else
                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        RiskProgressRing(
                                            percentage = risk.percentage,
                                            target = risk.target,
                                            size = 40.dp
                                        )
                                        Text(
                                            text = risk.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (risk.isRecovering)
                                                "${"%.1f".format(risk.percentage)}% - attend ${risk.requiredUnits} more"
                                            else
                                                "${"%.1f".format(risk.percentage)}% - only ${risk.safeBunks} safe bunk${if (risk.safeBunks == 1) "" else "s"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (risk.isRecovering) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedDate == LocalDate.now()) "Today's Classes" else "Classes on ${DateUtils.formatDateToHuman(selectedDateIso)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (todaySchedules.isNotEmpty()) {
                            Text(
                                text = "${todaySchedules.size} scheduled",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { showSimulator = true }) {
                            Text("What if?")
                        }
                    }
                }

                if (todaySchedules.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventAvailable,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "No Classes Scheduled",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "No classes found for this date. Tap '+' to add entries to your timetable or select another date above.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = todaySchedules,
                        key = { "${it.timetableEntry.id}_${it.session?.id ?: 0}" }
                    ) { item ->
                        val canMark = isSelectedDateInSemester
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                if (!canMark) return@rememberSwipeToDismissBoxState false
                                when (value) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        viewModel.markPresent(item)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "${item.subject.name} marked present",
                                                actionLabel = "Undo"
                                            )
                                            if (result == SnackbarResult.ActionPerformed) viewModel.undoLastAction()
                                        }
                                        false
                                    }
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        viewModel.markAbsent(item)
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "${item.subject.name} marked absent",
                                                actionLabel = "Undo"
                                            )
                                            if (result == SnackbarResult.ActionPerformed) viewModel.undoLastAction()
                                        }
                                        false
                                    }
                                    else -> false
                                }
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = canMark,
                            enableDismissFromEndToStart = canMark,
                            backgroundContent = {
                                val (color, icon, alignment) = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Triple(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        Icons.Default.Check,
                                        Alignment.CenterStart
                                    )
                                    SwipeToDismissBoxValue.EndToStart -> Triple(
                                        MaterialTheme.colorScheme.errorContainer,
                                        Icons.Default.Close,
                                        Alignment.CenterEnd
                                    )
                                    else -> Triple(Color.Transparent, null, Alignment.Center)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color)
                                        .padding(horizontal = 24.dp),
                                    contentAlignment = alignment
                                ) {
                                    if (icon != null) {
                                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        ) {
                        ClassCard(
                            timetableEntry = item.timetableEntry,
                            subject = item.subject,
                            session = item.session,
                            units = item.units,
                            onMarkPresent = {
                                if (isSelectedDateInSemester) {
                                    viewModel.markPresent(item)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cannot mark attendance outside semester range.")
                                    }
                                }
                            },
                            onMarkAbsent = {
                                if (isSelectedDateInSemester) {
                                    viewModel.markAbsent(item)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cannot mark attendance outside semester range.")
                                    }
                                }
                            },
                            onMarkBunked = {
                                if (isSelectedDateInSemester) {
                                    viewModel.markBunked(item)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cannot mark attendance outside semester range.")
                                    }
                                }
                            },
                            onMarkCancelled = {
                                if (isSelectedDateInSemester) {
                                    viewModel.markCancelled(item)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cannot mark attendance outside semester range.")
                                    }
                                }
                            },
                            onResetSession = {
                                if (isSelectedDateInSemester) {
                                    viewModel.resetSession(item)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Cannot modify attendance outside semester range.")
                                    }
                                }
                            },
                            onMoreOptions = {
                                activeSheetItem = item
                            },
                            onReschedule = {
                                reschedulingItem = item
                            },
                            onCancelReschedule = {
                                viewModel.cancelReschedule(item)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Reschedule reverted.")
                                }
                            }
                        )
                        }
                    }
                }
            }
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePickerDialog) {
        val initialMillis = remember(selectedDate) {
            selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val pickedLocalDate = Instant.ofEpochMilli(selectedMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            viewModel.selectDate(pickedLocalDate.format(DateUtils.isoDateFormatter))
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // What-if simulator sheet
    if (showSimulator) {
        WhatIfSimulatorSheet(
            presentUnits = overallSummary.presentUnits,
            absentUnits = overallSummary.absentUnits,
            targetPercentage = overallSummary.targetPercentage,
            onDismiss = { showSimulator = false }
        )
    }

    // Bottom Sheet for Editing Units
    activeSheetItem?.let { item ->
        EditUnitBottomSheet(
            subjectName = item.subject.name,
            units = item.units,
            onUnitStatusChange = { unitId, newStatus ->
                if (item.session != null) {
                    viewModel.updateUnitStatus(item.session.id, unitId, newStatus)
                }
            },
            onMarkAllStatus = { status ->
                viewModel.markAllSessionStatus(item, status)
            },
            onResetSession = {
                viewModel.resetSession(item)
            },
            onReschedule = {
                val target = item
                activeSheetItem = null
                reschedulingItem = target
            },
            onDismiss = { activeSheetItem = null }
        )
    }

    // Reschedule Dialog for a specific item
    reschedulingItem?.let { item ->
        RescheduleClassDialog(
            item = item,
            currentDateIso = selectedDateIso,
            onConfirm = { classItem, newDate, newStartTime, newEndTime, unitCount, reason ->
                viewModel.rescheduleClass(
                    item = classItem,
                    newDate = newDate,
                    newStartTime = newStartTime,
                    newEndTime = newEndTime,
                    unitCount = unitCount,
                    reason = reason
                )
                reschedulingItem = null
                scope.launch {
                    snackbarHostState.showSnackbar("Class rescheduled to ${DateUtils.formatDateToHuman(newDate)}")
                }
            },
            onDismiss = { reschedulingItem = null }
        )
    }

    // General Reschedule Dialog from FAB Quick Menu (only today's scheduled classes)
    if (showGeneralRescheduleDialog) {
        val todayReschedulableClasses = todaySchedules.filter { it.session?.rescheduledToDate == null }
        RescheduleClassDialog(
            item = null,
            availableClasses = todayReschedulableClasses,
            currentDateIso = selectedDateIso,
            onConfirm = { classItem, newDate, newStartTime, newEndTime, unitCount, reason ->
                viewModel.rescheduleClass(
                    item = classItem,
                    newDate = newDate,
                    newStartTime = newStartTime,
                    newEndTime = newEndTime,
                    unitCount = unitCount,
                    reason = reason
                )
                showGeneralRescheduleDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Class rescheduled to ${DateUtils.formatDateToHuman(newDate)}")
                }
            },
            onDismiss = { showGeneralRescheduleDialog = false }
        )
    }

    // Add Extra Class Dialog from FAB Quick Menu
    if (showAddExtraClassDialog) {
        AddExtraClassDialog(
            availableSubjects = allActiveSubjects,
            currentDateIso = selectedDateIso,
            onConfirm = { subjectId, dateIso, startTime, endTime, unitCount, room, teacher, notes ->
                viewModel.addExtraClass(
                    subjectId = subjectId,
                    dateIso = dateIso,
                    startTime = startTime,
                    endTime = endTime,
                    unitCount = unitCount,
                    notes = notes
                )
                showAddExtraClassDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("Extra class added for ${DateUtils.formatDateToHuman(dateIso)}")
                }
            },
            onDismiss = { showAddExtraClassDialog = false }
        )
    }
}

@Composable
private fun CurrentClassCard(info: HomeViewModel.CurrentClassInfo) {
    val subject = info.item.subject
    val room = info.item.timetableEntry.roomOverride.ifBlank { subject.room }
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Happening Now",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                val roomInfo = if (room.isNotBlank()) " - $room" else ""
                Text(
                    text = "Ends in ${info.minutesRemaining} min at ${info.endsAtDisplay}$roomInfo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WhatIfSimulatorSheet(
    presentUnits: Int,
    absentUnits: Int,
    targetPercentage: Double,
    onDismiss: () -> Unit
) {
    var attended by remember { mutableStateOf(0) }
    var missed by remember { mutableStateOf(0) }

    val projected = AttendanceCalculator.projectAttendance(
        presentUnits = presentUnits,
        absentUnits = absentUnits,
        futureAttended = attended,
        futureMissed = missed,
        targetPercentage = targetPercentage
    )
    val current = if (presentUnits + absentUnits > 0) {
        presentUnits.toDouble() / (presentUnits + absentUnits) * 100.0
    } else 0.0

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "What-if Simulator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Current: ${"%.1f".format(current)}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${"%.1f".format(projected)}%",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = if (projected >= targetPercentage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Text(
                text = if (projected >= targetPercentage)
                    "Above your ${targetPercentage.toInt()}% target"
                else
                    "Below your ${targetPercentage.toInt()}% target",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Attend", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (attended > 0) attended-- },
                            enabled = attended > 0
                        ) { Icon(Icons.Default.Remove, contentDescription = "Less") }
                        Text("$attended", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { attended++ }) {
                            Icon(Icons.Default.Add, contentDescription = "More")
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Miss", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (missed > 0) missed-- },
                            enabled = missed > 0
                        ) { Icon(Icons.Default.Remove, contentDescription = "Less") }
                        Text("$missed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { missed++ }) { Icon(Icons.Default.Add, contentDescription = "More") }
                    }
                }
            }
            TextButton(onClick = onDismiss) { Text("Close") }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
