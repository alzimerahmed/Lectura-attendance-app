/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_units",
    foreignKeys = [
        ForeignKey(
            entity = AttendanceSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sessionId"])
    ]
)
data class AttendanceUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val unitIndex: Int, // 0, 1, 2...
    val status: String, // UNMARKED, PRESENT, ABSENT, CANCELLED
    val markedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
