package com.example.utils

import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ParsedVoiceExpense(
    val category: ExpenseCategory,
    val description: String,
    val amount: Int
)

object VoiceExpenseParser {

    fun parseSpokenText(input: String): ParsedVoiceExpense? {
        val cleaned = input.trim().replace(",", "")
        if (cleaned.isBlank()) return null

        // Find number in the input
        val numberRegex = Regex("""(\d+)""")
        val match = numberRegex.find(cleaned)
        val amount = match?.value?.toIntOrNull() ?: return null
        if (amount <= 0) return null

        // Remove amount and currency words to extract description
        val rawWords = cleaned
            .replace(numberRegex, "")
            .replace(Regex("""\b(rupees|rupee|rs|inr|for|on|spent|paid)\b""", RegexOption.IGNORE_CASE), "")
            .replace("₹", "")
            .trim()
            .replace(Regex("""\s+"""), " ")

        val lower = cleaned.lowercase(Locale.ROOT)
        val category = when {
            lower.contains("food") || lower.contains("lunch") || lower.contains("dinner") ||
                lower.contains("breakfast") || lower.contains("restaurant") || lower.contains("tea") ||
                lower.contains("coffee") || lower.contains("snack") || lower.contains("tiffin") ||
                lower.contains("pizza") || lower.contains("biryani") -> ExpenseCategory.FOOD

            lower.contains("travel") || lower.contains("bus") || lower.contains("train") ||
                lower.contains("metro") || lower.contains("auto") || lower.contains("cab") ||
                lower.contains("taxi") || lower.contains("uber") || lower.contains("ola") ||
                lower.contains("petrol") || lower.contains("fuel") || lower.contains("ticket") -> ExpenseCategory.TRAVEL

            lower.contains("grocer") || lower.contains("vegetable") || lower.contains("fruit") ||
                lower.contains("milk") || lower.contains("ration") || lower.contains("market") ||
                lower.contains("bread") || lower.contains("oil") || lower.contains("rice") -> ExpenseCategory.GROCERIES

            else -> ExpenseCategory.OTHERS
        }

        val description = if (rawWords.isNotBlank()) {
            rawWords.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        } else {
            when (category) {
                ExpenseCategory.FOOD -> "Meal & Refreshments"
                ExpenseCategory.TRAVEL -> "Local Commute"
                ExpenseCategory.GROCERIES -> "Daily Groceries"
                ExpenseCategory.OTHERS -> "General Expense"
            }
        }

        return ParsedVoiceExpense(
            category = category,
            description = description,
            amount = amount
        )
    }

    fun toExpenseEntity(parsed: ParsedVoiceExpense): ExpenseEntity {
        val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date())
        return ExpenseEntity(
            category = parsed.category.displayName,
            title = parsed.category.displayName,
            description = parsed.description,
            amount = parsed.amount,
            timeFormatted = timeStr,
            dateLabel = "Today",
            periodTag = "THIS_MONTH",
            isToday = true,
            sortOrder = 0, // Appears promptly in Today's Expenses
            timestamp = System.currentTimeMillis()
        )
    }
}
