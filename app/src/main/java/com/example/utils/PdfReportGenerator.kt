package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.AnalyticsPeriod
import com.example.data.CategoryBreakdown
import com.example.data.ExpenseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfReportResult(
    val file: File,
    val savedDisplayPath: String,
    val totalAmount: Int,
    val itemCount: Int
)

object PdfReportGenerator {

    fun formatIndianRupee(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("en", "IN"))
        return "₹${formatter.format(amount)}"
    }

    suspend fun generateExpenseReportPdf(
        context: Context,
        period: AnalyticsPeriod,
        breakdowns: List<CategoryBreakdown>,
        expenses: List<ExpenseEntity>,
        totalAmount: Int
    ): Result<PdfReportResult> = withContext(Dispatchers.IO) {
        runCatching {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // 1. Background
            paint.color = Color.parseColor("#F7F9FF")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), paint)

            // 2. Navy Header
            paint.color = Color.parseColor("#071D4B")
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 115f, paint)

            // Tricolor accent stripe under header
            paint.color = Color.parseColor("#FF8A00")
            canvas.drawRect(0f, 115f, pageWidth / 3f, 121f, paint)
            paint.color = Color.WHITE
            canvas.drawRect(pageWidth / 3f, 115f, (pageWidth * 2) / 3f, 121f, paint)
            paint.color = Color.parseColor("#16C900")
            canvas.drawRect((pageWidth * 2) / 3f, 115f, pageWidth.toFloat(), 121f, paint)

            // Branding in Header
            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 26f
            canvas.drawText("HISAB", 40f, 55f, paint)
            val hisabWidth = paint.measureText("HISAB ")
            paint.color = Color.parseColor("#FFD400")
            canvas.drawText("KHATA", 40f + hisabWidth, 55f, paint)

            paint.color = Color.WHITE
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 13f
            canvas.drawText("Smart Expense Tracker — Official Financial Report", 40f, 80f, paint)

            val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(Date())
            paint.textSize = 11f
            paint.color = Color.parseColor("#D6E9FF")
            canvas.drawText("Period: ${period.label}   |   Generated: $dateStr", 40f, 100f, paint)

            // 3. Summary Box
            paint.color = Color.WHITE
            val summaryRect = RectF(40f, 145f, pageWidth - 40f, 225f)
            canvas.drawRoundRect(summaryRect, 14f, 14f, paint)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f
            paint.color = Color.parseColor("#155BFF")
            canvas.drawRoundRect(summaryRect, 14f, 14f, paint)
            paint.style = Paint.Style.FILL

            paint.color = Color.parseColor("#53658D")
            paint.textSize = 13f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("TOTAL SPENT (${period.label.uppercase()})", 60f, 175f, paint)

            paint.color = Color.parseColor("#071D4B")
            paint.textSize = 28f
            canvas.drawText(formatIndianRupee(totalAmount), 60f, 208f, paint)

            // 4. Category Breakdown Section
            paint.color = Color.parseColor("#101C3D")
            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Category Breakdown", 40f, 260f, paint)

            var yPos = 285f
            for (item in breakdowns) {
                paint.color = Color.WHITE
                val rowRect = RectF(40f, yPos, pageWidth - 40f, yPos + 36f)
                canvas.drawRoundRect(rowRect, 8f, 8f, paint)

                val dotColor = when (item.category.displayName) {
                    "Food" -> Color.parseColor("#FF8A00")
                    "Travel" -> Color.parseColor("#087BFF")
                    "Groceries" -> Color.parseColor("#16C900")
                    else -> Color.parseColor("#8439E8")
                }
                paint.color = dotColor
                canvas.drawCircle(62f, yPos + 18f, 7f, paint)

                paint.color = Color.parseColor("#101C3D")
                paint.textSize = 13f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(item.category.displayName, 80f, yPos + 23f, paint)

                canvas.drawText(formatIndianRupee(item.amount), 360f, yPos + 23f, paint)

                paint.color = Color.parseColor("#53658D")
                canvas.drawText("${item.percentage}%", 495f, yPos + 23f, paint)

                yPos += 44f
            }

            // 5. Recent Expense Transactions Section
            yPos += 18f
            paint.color = Color.parseColor("#101C3D")
            paint.textSize = 16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Expense Transactions (${expenses.size} items)", 40f, yPos, paint)
            yPos += 16f

            // Table Header
            paint.color = Color.parseColor("#0B2055")
            canvas.drawRoundRect(RectF(40f, yPos, pageWidth - 40f, yPos + 28f), 6f, 6f, paint)
            paint.color = Color.WHITE
            paint.textSize = 11f
            canvas.drawText("CATEGORY", 54f, yPos + 18f, paint)
            canvas.drawText("DESCRIPTION", 150f, yPos + 18f, paint)
            canvas.drawText("TIME / DATE", 365f, yPos + 18f, paint)
            canvas.drawText("AMOUNT", 480f, yPos + 18f, paint)
            yPos += 34f

            val maxRows = expenses.take(11)
            for (expense in maxRows) {
                paint.color = Color.WHITE
                canvas.drawRoundRect(RectF(40f, yPos, pageWidth - 40f, yPos + 26f), 4f, 4f, paint)

                paint.color = Color.parseColor("#101C3D")
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 11f
                canvas.drawText(expense.category, 54f, yPos + 17f, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.color = Color.parseColor("#53658D")
                val desc = if (expense.description.length > 32) {
                    expense.description.take(29) + "..."
                } else {
                    expense.description
                }
                canvas.drawText(desc, 150f, yPos + 17f, paint)
                canvas.drawText("${expense.dateLabel}, ${expense.timeFormatted}", 365f, yPos + 17f, paint)

                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.color = Color.parseColor("#101C3D")
                canvas.drawText(formatIndianRupee(expense.amount), 480f, yPos + 17f, paint)

                yPos += 30f
            }

            // Footer
            paint.color = Color.parseColor("#53658D")
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10f
            canvas.drawText(
                "Generated by HISAB KHATA • Track • Analyze • Save",
                40f,
                pageHeight - 25f,
                paint
            )

            pdfDocument.finishPage(page)

            // Save to app cache reports directory for FileProvider sharing/viewing
            val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
            val safePeriod = period.label.replace(" ", "_")
            val fileName = "Hisab_Khata_Report_${safePeriod}.pdf"
            val reportFile = File(reportsDir, fileName)
            FileOutputStream(reportFile).use { out ->
                pdfDocument.writeTo(out)
            }

            // Also save to public Downloads on Android 10+ via MediaStore (zero permissions required)
            var savedDisplayPath = reportFile.absolutePath
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                runCatching {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                        put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { outStream ->
                            reportFile.inputStream().use { inStream ->
                                inStream.copyTo(outStream)
                            }
                        }
                        savedDisplayPath = "Downloads/$fileName"
                    }
                }
            }

            pdfDocument.close()

            PdfReportResult(
                file = reportFile,
                savedDisplayPath = savedDisplayPath,
                totalAmount = totalAmount,
                itemCount = expenses.size
            )
        }
    }

    fun shareOrOpenPdf(context: Context, file: File) {
        runCatching {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Hisab Khata Expense Report")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Share or Open PDF Report").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }
}
