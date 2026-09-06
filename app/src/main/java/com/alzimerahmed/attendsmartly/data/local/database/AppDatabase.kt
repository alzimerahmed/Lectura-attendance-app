/*
 * AttendSmartly (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.attendsmartly.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alzimerahmed.attendsmartly.data.local.dao.AttendanceDao
import com.alzimerahmed.attendsmartly.data.local.dao.HolidayDao
import com.alzimerahmed.attendsmartly.data.local.dao.SubjectDao
import com.alzimerahmed.attendsmartly.data.local.dao.TimetableDao
import com.alzimerahmed.attendsmartly.data.local.entity.AttendanceSessionEntity
import com.alzimerahmed.attendsmartly.data.local.entity.AttendanceUnitEntity
import com.alzimerahmed.attendsmartly.data.local.entity.HolidayEntity
import com.alzimerahmed.attendsmartly.data.local.entity.SubjectEntity
import com.alzimerahmed.attendsmartly.data.local.entity.TimetableEntryEntity

@Database(
    entities = [
        SubjectEntity::class,
        TimetableEntryEntity::class,
        AttendanceSessionEntity::class,
        AttendanceUnitEntity::class,
        HolidayEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun timetableDao(): TimetableDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun holidayDao(): HolidayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AttendSmartly_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
