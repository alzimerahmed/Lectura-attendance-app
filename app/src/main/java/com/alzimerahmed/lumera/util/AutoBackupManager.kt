/*
 * Lumera (2026)
 * © alzimer ahmed — github.com/alzimerahmed84
 * Licensed under the GNU GPL v3 License
 * Do not remove or alter this notice. - Per GPL-3.0 Section 4 & Section 5
 */

package com.alzimerahmed.lumera.util

import android.content.Context
import android.content.SharedPreferences
import com.alzimerahmed.lumera.data.repository.LumeraRepository
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Automatic local backups: daily JSON snapshots kept in app-private storage,
 * pruned to the most recent [MAX_BACKUPS]. Complements the manual SAF export.
 */
object AutoBackupManager {

    private const val PREFS_NAME = "lumera_backup_prefs"
    private const val KEY_LAST_BACKUP_AT = "last_backup_at"
    const val MAX_BACKUPS = 7

    private fun backupsDir(context: Context): File =
        File(context.filesDir, "backups").apply { if (!exists()) mkdirs() }

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun performAutoBackup(context: Context, repository: LumeraRepository): Boolean {
        return try {
            val json = ExportImportUtils.exportToJson(repository)
            val stamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                .withZone(ZoneId.systemDefault()).format(Instant.now())
            val file = File(backupsDir(context), "auto_backup_$stamp.json")
            file.writeText(json)
            pruneOldBackups(context)
            prefs(context).edit().putLong(KEY_LAST_BACKUP_AT, System.currentTimeMillis()).apply()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun listBackups(context: Context): List<File> =
        backupsDir(context).listFiles { f -> f.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()

    fun lastBackupAt(context: Context): Long =
        prefs(context).getLong(KEY_LAST_BACKUP_AT, 0L)

    fun formatBackupTime(context: Context, millis: Long): String {
        if (millis <= 0L) return "Never"
        val dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
        val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
        return if (dateTime.toLocalDate() == today) {
            "Today, " + DateTimeFormatter.ofPattern("HH:mm").format(dateTime)
        } else {
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm").format(dateTime)
        }
    }

    suspend fun restoreFromBackup(
        context: Context,
        repository: LumeraRepository,
        backupFile: File
    ): Pair<Boolean, String> {
        return try {
            ExportImportUtils.restoreFromJson(backupFile.readText(), repository)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, "Restore failed: ${e.localizedMessage ?: e.message}")
        }
    }

    private fun pruneOldBackups(context: Context) {
        val backups = listBackups(context)
        if (backups.size > MAX_BACKUPS) {
            backups.drop(MAX_BACKUPS).forEach { it.delete() }
        }
    }
}
