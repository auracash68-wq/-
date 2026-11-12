package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hisab_khata_prefs")

class ExpenseRepository(
    private val context: Context,
    private val expenseDao: ExpenseDao
) {
    companion object {
        private val KEY_INITIAL_SEED_DONE = booleanPreferencesKey("initial_seed_done")
        private val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        private val KEY_LAST_BACKUP_TIME = stringPreferencesKey("last_backup_time")

        fun defaultSeedExpenses(): List<ExpenseEntity> {
            val now = System.currentTimeMillis()
            return listOf(
                // Exact 3 Today's Expenses shown on Home Screen Reference (Image 2, left phone)
                ExpenseEntity(
                    category = "Food",
                    title = "Food",
                    description = "Lunch at Restaurant",
                    amount = 250,
                    timeFormatted = "8:15 PM",
                    dateLabel = "Today",
                    periodTag = "THIS_MONTH",
                    isToday = true,
                    sortOrder = 1,
                    timestamp = now - 60_000L
                ),
                ExpenseEntity(
                    category = "Travel",
                    title = "Travel",
                    description = "Bus Ticket",
                    amount = 120,
                    timeFormatted = "5:42 PM",
                    dateLabel = "Today",
                    periodTag = "THIS_MONTH",
                    isToday = true,
                    sortOrder = 2,
                    timestamp = now - 120_000L
                ),
                ExpenseEntity(
                    category = "Groceries",
                    title = "Groceries",
                    description = "Vegetables & Fruits",
                    amount = 180,
                    timeFormatted = "1:20 PM",
                    dateLabel = "Today",
                    periodTag = "THIS_MONTH",
                    isToday = true,
                    sortOrder = 3,
                    timestamp = now - 180_000L
                ),

                // Earlier This Month expenses so that This Month totals match Reference Image 2 strictly:
                // Food = 2,268 (42%), Travel = 1,512 (28%), Groceries = 972 (18%), Others = 648 (12%)
                // Total Spent = ₹5,400
                ExpenseEntity(
                    category = "Food",
                    title = "Food",
                    description = "Monthly Tiffin & Dining",
                    amount = 1150,
                    timeFormatted = "9:10 PM",
                    dateLabel = "10 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 10,
                    timestamp = now - 86_400_000L * 2
                ),
                ExpenseEntity(
                    category = "Food",
                    title = "Food",
                    description = "Family Dinner & Sweets",
                    amount = 868,
                    timeFormatted = "8:45 PM",
                    dateLabel = "05 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 11,
                    timestamp = now - 86_400_000L * 6
                ),
                ExpenseEntity(
                    category = "Travel",
                    title = "Travel",
                    description = "Metro Card & Auto Rides",
                    amount = 840,
                    timeFormatted = "10:15 AM",
                    dateLabel = "09 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 12,
                    timestamp = now - 86_400_000L * 3
                ),
                ExpenseEntity(
                    category = "Travel",
                    title = "Travel",
                    description = "Intercity Train Ticket",
                    amount = 552,
                    timeFormatted = "6:30 PM",
                    dateLabel = "03 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 13,
                    timestamp = now - 86_400_000L * 8
                ),
                ExpenseEntity(
                    category = "Groceries",
                    title = "Groceries",
                    description = "Supermarket Monthly Ration",
                    amount = 512,
                    timeFormatted = "7:05 PM",
                    dateLabel = "08 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 14,
                    timestamp = now - 86_400_000L * 4
                ),
                ExpenseEntity(
                    category = "Groceries",
                    title = "Groceries",
                    description = "Milk, Dairy & Bakery",
                    amount = 280,
                    timeFormatted = "8:20 AM",
                    dateLabel = "02 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 15,
                    timestamp = now - 86_400_000L * 9
                ),
                ExpenseEntity(
                    category = "Others",
                    title = "Others",
                    description = "Mobile & Broadband Bill",
                    amount = 399,
                    timeFormatted = "11:00 AM",
                    dateLabel = "07 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 16,
                    timestamp = now - 86_400_000L * 5
                ),
                ExpenseEntity(
                    category = "Others",
                    title = "Others",
                    description = "Pharmacy & Household Items",
                    amount = 249,
                    timeFormatted = "4:15 PM",
                    dateLabel = "01 Nov",
                    periodTag = "THIS_MONTH",
                    isToday = false,
                    sortOrder = 17,
                    timestamp = now - 86_400_000L * 10
                ),

                // Last Month expenses for dynamic Analytics filter ("Last Month")
                ExpenseEntity(
                    category = "Food",
                    title = "Food",
                    description = "Restaurants & Tiffin (Last Month)",
                    amount = 1920,
                    timeFormatted = "8:30 PM",
                    dateLabel = "Oct",
                    periodTag = "LAST_MONTH",
                    isToday = false,
                    sortOrder = 30,
                    timestamp = now - 86_400_000L * 35
                ),
                ExpenseEntity(
                    category = "Travel",
                    title = "Travel",
                    description = "Bus, Metro & Fuel (Last Month)",
                    amount = 1440,
                    timeFormatted = "6:00 PM",
                    dateLabel = "Oct",
                    periodTag = "LAST_MONTH",
                    isToday = false,
                    sortOrder = 31,
                    timestamp = now - 86_400_000L * 38
                ),
                ExpenseEntity(
                    category = "Groceries",
                    title = "Groceries",
                    description = "Vegetables & Ration (Last Month)",
                    amount = 960,
                    timeFormatted = "5:15 PM",
                    dateLabel = "Oct",
                    periodTag = "LAST_MONTH",
                    isToday = false,
                    sortOrder = 32,
                    timestamp = now - 86_400_000L * 40
                ),
                ExpenseEntity(
                    category = "Others",
                    title = "Others",
                    description = "Utility Bills & Misc (Last Month)",
                    amount = 480,
                    timeFormatted = "2:00 PM",
                    dateLabel = "Oct",
                    periodTag = "LAST_MONTH",
                    isToday = false,
                    sortOrder = 33,
                    timestamp = now - 86_400_000L * 42
                ),

                // Earlier This Year expenses for dynamic Analytics filter ("This Year")
                ExpenseEntity(
                    category = "Food",
                    title = "Food",
                    description = "Dining & Food (Jan - Sep)",
                    amount = 14812,
                    timeFormatted = "7:45 PM",
                    dateLabel = "Jan - Sep",
                    periodTag = "THIS_YEAR_EARLIER",
                    isToday = false,
                    sortOrder = 50,
                    timestamp = now - 86_400_000L * 120
                ),
                ExpenseEntity(
                    category = "Travel",
                    title = "Travel",
                    description = "Travel & Commute (Jan - Sep)",
                    amount = 9848,
                    timeFormatted = "9:30 AM",
                    dateLabel = "Jan - Sep",
                    periodTag = "THIS_YEAR_EARLIER",
                    isToday = false,
                    sortOrder = 51,
                    timestamp = now - 86_400_000L * 130
                ),
                ExpenseEntity(
                    category = "Groceries",
                    title = "Groceries",
                    description = "Groceries & Provisions (Jan - Sep)",
                    amount = 6468,
                    timeFormatted = "6:20 PM",
                    dateLabel = "Jan - Sep",
                    periodTag = "THIS_YEAR_EARLIER",
                    isToday = false,
                    sortOrder = 52,
                    timestamp = now - 86_400_000L * 140
                ),
                ExpenseEntity(
                    category = "Others",
                    title = "Others",
                    description = "Bills & Miscellaneous (Jan - Sep)",
                    amount = 4272,
                    timeFormatted = "1:10 PM",
                    dateLabel = "Jan - Sep",
                    periodTag = "THIS_YEAR_EARLIER",
                    isToday = false,
                    sortOrder = 53,
                    timestamp = now - 86_400_000L * 150
                )
            )
        }
    }

    val allExpensesFlow: Flow<List<ExpenseEntity>> = expenseDao.getAllExpensesFlow()

    val selectedLanguageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        val code = prefs[KEY_SELECTED_LANGUAGE] ?: AppLanguage.ENGLISH.code
        AppLanguage.fromCode(code)
    }

    val lastBackupTimeFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_BACKUP_TIME]
    }

    suspend fun ensureSeeded() {
        val prefs = context.dataStore.data.first()
        val alreadySeeded = prefs[KEY_INITIAL_SEED_DONE] ?: false
        if (!alreadySeeded) {
            if (expenseDao.getExpenseCount() == 0) {
                expenseDao.insertAll(defaultSeedExpenses())
            }
            context.dataStore.edit { it[KEY_INITIAL_SEED_DONE] = true }
        }
    }

    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun clearAllExpenses() {
        expenseDao.clearAllExpenses()
    }

    suspend fun restoreDefaultExpenses() {
        expenseDao.clearAllExpenses()
        expenseDao.insertAll(defaultSeedExpenses())
    }

    suspend fun replaceAllExpenses(expenses: List<ExpenseEntity>) {
        expenseDao.clearAllExpenses()
        expenseDao.insertAll(expenses)
    }

    suspend fun getAllExpensesOnce(): List<ExpenseEntity> {
        return expenseDao.getAllExpensesOnce()
    }

    suspend fun setSelectedLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_LANGUAGE] = language.code
        }
    }

    suspend fun setLastBackupTime(formattedTime: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LAST_BACKUP_TIME] = formattedTime
        }
    }
}
