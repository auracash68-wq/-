package com.example.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
    val file: File,
    val timestampFormatted: String,
    val itemCount: Int,
    val totalAmount: Int
)

object BackupManager {

    private const val BACKUP_DIR = "backups"
    private const val BACKUP_FILE_NAME = "hisab_khata_backup.json"

    private fun getBackupFile(context: Context): File {
        val dir = File(context.filesDir, BACKUP_DIR).apply { mkdirs() }
        return File(dir, BACKUP_FILE_NAME)
    }

    suspend fun performLocalBackup(
        context: Context,
        expenses: List<ExpenseEntity>
    ): Result<BackupMetadata> = withContext(Dispatchers.IO) {
        runCatching {
            val nowFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
            val root = JSONObject()
            root.put("app", "HISAB KHATA")
            root.put("version", 1)
            root.put("backupDate", nowFormatted)
            root.put("itemCount", expenses.size)

            val array = JSONArray()
            var total = 0
            for (expense in expenses) {
                total += expense.amount
                val obj = JSONObject().apply {
                    put("category", expense.category)
                    put("title", expense.title)
                    put("description", expense.description)
                    put("amount", expense.amount)
                    put("timeFormatted", expense.timeFormatted)
                    put("dateLabel", expense.dateLabel)
                    put("periodTag", expense.periodTag)
                    put("isToday", expense.isToday)
                    put("sortOrder", expense.sortOrder)
                    put("timestamp", expense.timestamp)
                }
                array.put(obj)
            }
            root.put("totalAmount", total)
            root.put("expenses", array)

            val file = getBackupFile(context)
            file.writeText(root.toString(2))

            BackupMetadata(
                file = file,
                timestampFormatted = nowFormatted,
                itemCount = expenses.size,
                totalAmount = total
            )
        }
    }

    suspend fun getExistingBackupMetadata(context: Context): BackupMetadata? = withContext(Dispatchers.IO) {
        val file = getBackupFile(context)
        if (!file.exists()) return@withContext null
        runCatching {
            val root = JSONObject(file.readText())
            BackupMetadata(
                file = file,
                timestampFormatted = root.optString("backupDate", "Saved Backup"),
                itemCount = root.optInt("itemCount", 0),
                totalAmount = root.optInt("totalAmount", 0)
            )
        }.getOrNull()
    }

    suspend fun restoreFromLocalBackup(context: Context): Result<List<ExpenseEntity>> = withContext(Dispatchers.IO) {
        runCatching {
            val file = getBackupFile(context)
            if (!file.exists()) {
                throw IllegalStateException("No local backup file found. Create a backup first.")
            }
            val root = JSONObject(file.readText())
            val array = root.getJSONArray("expenses")
            val restored = mutableListOf<ExpenseEntity>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                restored.add(
                    ExpenseEntity(
                        category = obj.optString("category", "Others"),
                        title = obj.optString("title", obj.optString("category", "Others")),
                        description = obj.optString("description", "Restored Expense"),
                        amount = obj.optInt("amount", 0),
                        timeFormatted = obj.optString("timeFormatted", "12:00 PM"),
                        dateLabel = obj.optString("dateLabel", "Today"),
                        periodTag = obj.optString("periodTag", "THIS_MONTH"),
                        isToday = obj.optBoolean("isToday", false),
                        sortOrder = obj.optInt("sortOrder", 50),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            restored
        }
    }

    fun shareBackupFile(context: Context) {
        val file = getBackupFile(context)
        if (!file.exists()) return
        runCatching {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Hisab Khata Data Backup")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                Intent.createChooser(intent, "Export Backup File").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}
