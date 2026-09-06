/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alzimerahmed.lumera.data.local.dao.AssignmentDao
import com.alzimerahmed.lumera.data.local.dao.AttendanceDao
import com.alzimerahmed.lumera.data.local.dao.ExamDao
import com.alzimerahmed.lumera.data.local.dao.HolidayDao
import com.alzimerahmed.lumera.data.local.dao.SubjectDao
import com.alzimerahmed.lumera.data.local.dao.TimetableDao
import com.alzimerahmed.lumera.data.local.entity.AssignmentEntity
import com.alzimerahmed.lumera.data.local.entity.AttendanceSessionEntity
import com.alzimerahmed.lumera.data.local.entity.AttendanceUnitEntity
import com.alzimerahmed.lumera.data.local.entity.ExamEntity
import com.alzimerahmed.lumera.data.local.entity.HolidayEntity
import com.alzimerahmed.lumera.data.local.entity.SubjectEntity
import com.alzimerahmed.lumera.data.local.entity.TimetableEntryEntity

@Database(
    entities = [
        SubjectEntity::class,
        TimetableEntryEntity::class,
        AttendanceSessionEntity::class,
        AttendanceUnitEntity::class,
        HolidayEntity::class,
        AssignmentEntity::class,
        ExamEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun timetableDao(): TimetableDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun holidayDao(): HolidayDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun examDao(): ExamDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS assignments (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        subjectId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        dueDateIso TEXT NOT NULL,
                        notes TEXT NOT NULL,
                        isDone INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )"""
                )
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS exams (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        subjectId INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        examDateIso TEXT NOT NULL,
                        syllabus TEXT NOT NULL,
                        grade TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )"""
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Lumera_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
