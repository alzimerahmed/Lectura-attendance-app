/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.domain.model

enum class SubjectType(val displayName: String) {
    LECTURE("Lecture"),
    TUTORIAL("Tutorial"),
    PRACTICAL_LAB("Practical Lab"),
    WORKSHOP("Workshop"),
    SEMINAR("Seminar"),
    OTHER("Other")
}
