/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera

import com.alzimerahmed.lumera.domain.calculator.AttendanceCalculator
import com.alzimerahmed.lumera.domain.model.AttendanceStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AttendanceCalculatorTest {

    @Test
    fun testEmptyAttendance() {
        val summary = AttendanceCalculator.calculate(emptyList(), targetPercentage = 75.0)
        assertEquals(0, summary.totalConductedUnits)
        assertEquals(0, summary.presentUnits)
        assertEquals(0.0, summary.percentage, 0.01)
        assertEquals(0, summary.safeBunks)
        assertEquals(0, summary.requiredUnitsToTarget)
    }

    @Test
    fun testSafeBunksCalculation() {
        // 8 Present out of 10 Conducted = 80% (Target 75%)
        val statuses = listOf(
            AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT,
            AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT,
            AttendanceStatus.ABSENT, AttendanceStatus.ABSENT
        )
        val summary = AttendanceCalculator.calculate(statuses, targetPercentage = 75.0)
        assertEquals(10, summary.totalConductedUnits)
        assertEquals(8, summary.presentUnits)
        assertEquals(80.0, summary.percentage, 0.01)

        // 9 Present out of 10 Conducted = 90% (Target 75%)
        val statuses2 = listOf(
            AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT,
            AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT, AttendanceStatus.PRESENT,
            AttendanceStatus.PRESENT, AttendanceStatus.ABSENT
        )
        val summary2 = AttendanceCalculator.calculate(statuses2, targetPercentage = 75.0)
        assertEquals(10, summary2.totalConductedUnits)
        assertEquals(9, summary2.presentUnits)
        assertEquals(90.0, summary2.percentage, 0.01)
        assertEquals(2, summary2.safeBunks)
    }

    @Test
    fun testPrecisionSafeBunks() {
        // 14 Present out of 19 Conducted = 73.68% (Target 70%)
        // If 1 miss: 14 / 20 = 70.0% >= 70% -> Exactly 1 safe bunk!
        val summary = AttendanceCalculator.calculate(presentUnits = 14, absentUnits = 5, targetPercentage = 70.0)
        assertEquals(1, summary.safeBunks)
    }

    @Test
    fun testRequiredClassesCalculation() {
        // 5 Present out of 10 Conducted = 50% (Target 75%)
        val statuses = List(5) { AttendanceStatus.PRESENT } + List(5) { AttendanceStatus.ABSENT }
        val summary = AttendanceCalculator.calculate(statuses, targetPercentage = 75.0)
        assertEquals(10, summary.totalConductedUnits)
        assertEquals(5, summary.presentUnits)
        assertEquals(50.0, summary.percentage, 0.01)
        assertEquals(10, summary.requiredUnitsToTarget)
    }

    @Test
    fun testTimetableChangePastAttendanceIntegrity() {
        // Old timetable had 4 classes attended in the past, new timetable has 2 classes attended
        val pastSessionsUnits = listOf(
            AttendanceStatus.PRESENT,
            AttendanceStatus.PRESENT,
            AttendanceStatus.PRESENT,
            AttendanceStatus.ABSENT
        )
        val newTimetableUnits = listOf(
            AttendanceStatus.PRESENT,
            AttendanceStatus.PRESENT
        )
        val combinedAllHistory = pastSessionsUnits + newTimetableUnits
        val summary = AttendanceCalculator.calculate(combinedAllHistory, targetPercentage = 75.0)

        // 5 Present out of 6 Conducted = 83.33%
        assertEquals(6, summary.totalConductedUnits)
        assertEquals(5, summary.presentUnits)
        assertEquals(1, summary.absentUnits)
        assertEquals(83.33, summary.percentage, 0.01)
        assertTrue(summary.percentage >= 75.0)
    }

    @Test
    fun testRecoveryPlan() {
        // 5 present / 10 total = 50%, target 75% -> need 10 consecutive presents
        val plan = AttendanceCalculator.recoveryPlan(presentUnits = 5, absentUnits = 5, targetPercentage = 75.0)
        assertTrue(plan.isRecovering)
        assertEquals(10, plan.requiredConsecutiveUnits)
        assertEquals(75.0, plan.projectedPercentage, 0.01)

        // Already above target -> not recovering
        val fine = AttendanceCalculator.recoveryPlan(presentUnits = 9, absentUnits = 1, targetPercentage = 75.0)
        assertTrue(!fine.isRecovering)
        assertEquals(0, fine.requiredConsecutiveUnits)
    }

    @Test
    fun testProjectAttendance() {
        // 6/8 = 75%; attend 2 more -> 8/10 = 80%
        assertEquals(80.0, AttendanceCalculator.projectAttendance(6, 2, futureAttended = 2), 0.01)
        // miss 2 -> 6/10 = 60%
        assertEquals(60.0, AttendanceCalculator.projectAttendance(6, 2, futureMissed = 2), 0.01)
    }

    @Test
    fun testSimulate() {
        // 6/8 = 75%, bunk 1 -> 6/9 = 66.67%
        assertEquals(66.66, AttendanceCalculator.simulate(6, 2, bunkUnits = 1), 0.01)
        // attend 1 more -> 7/9 = 77.78%
        assertEquals(77.78, AttendanceCalculator.simulate(6, 2, bunkUnits = -1), 0.01)
    }
}