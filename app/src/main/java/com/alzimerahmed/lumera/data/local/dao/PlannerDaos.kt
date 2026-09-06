/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alzimerahmed.lumera.data.local.entity.AssignmentEntity
import com.alzimerahmed.lumera.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    @Query("SELECT * FROM assignments ORDER BY isDone ASC, dueDateIso ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE subjectId = :subjectId ORDER BY dueDateIso ASC")
    fun getAssignmentsForSubject(subjectId: Long): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE isDone = 0 AND dueDateIso >= :todayIso AND dueDateIso <= :untilIso")
    suspend fun getAssignmentsDueBetween(todayIso: String, untilIso: String): List<AssignmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    @Query("DELETE FROM assignments WHERE subjectId = :subjectId")
    suspend fun deleteAssignmentsForSubject(subjectId: Long)
}

@Dao
interface ExamDao {

    @Query("SELECT * FROM exams ORDER BY examDateIso ASC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE subjectId = :subjectId ORDER BY examDateIso ASC")
    fun getExamsForSubject(subjectId: Long): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Delete
    suspend fun deleteExam(exam: ExamEntity)

    @Query("DELETE FROM exams WHERE subjectId = :subjectId")
    suspend fun deleteExamsForSubject(subjectId: Long)
}
