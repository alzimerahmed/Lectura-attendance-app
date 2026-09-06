/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holidays")
data class HolidayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val title: String,
    val notes: String = ""
)
