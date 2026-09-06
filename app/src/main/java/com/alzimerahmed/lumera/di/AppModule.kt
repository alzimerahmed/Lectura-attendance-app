/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.di

import android.content.Context
import com.alzimerahmed.lumera.data.local.database.AppDatabase
import com.alzimerahmed.lumera.data.preferences.UserPreferencesRepository
import com.alzimerahmed.lumera.data.repository.LumeraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository =
        UserPreferencesRepository(context)

    @Provides
    @Singleton
    fun provideLumeraRepository(database: AppDatabase): LumeraRepository =
        LumeraRepository(
            subjectDao = database.subjectDao(),
            timetableDao = database.timetableDao(),
            attendanceDao = database.attendanceDao(),
            holidayDao = database.holidayDao(),
            assignmentDao = database.assignmentDao(),
            examDao = database.examDao()
        )
}
