package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory(val displayName: String) {
    FOOD("Food"),
    TRAVEL("Travel"),
    GROCERIES("Groceries"),
    OTHERS("Others");

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return entries.firstOrNull {
                it.displayName.equals(value.trim(), ignoreCase = true) ||
                    it.name.equals(value.trim(), ignoreCase = true)
            } ?: OTHERS
        }
    }
}

enum class AnalyticsPeriod(val label: String) {
    THIS_MONTH("This Month"),
    LAST_MONTH("Last Month"),
    THIS_YEAR("This Year")
}

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val title: String,
    val description: String,
    val amount: Int,
    val timeFormatted: String,
    val dateLabel: String = "Today",
    val periodTag: String = "THIS_MONTH", // THIS_MONTH, LAST_MONTH, THIS_YEAR_EARLIER
    val isToday: Boolean = true,
    val sortOrder: Int = 100,
    val timestamp: Long = System.currentTimeMillis()
)

data class CategoryBreakdown(
    val category: ExpenseCategory,
    val amount: Int,
    val percentage: Int
)
