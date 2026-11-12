package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AnalyticsPeriod
import com.example.data.AppLanguage
import com.example.data.CategoryBreakdown
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.data.ExpenseRepository
import com.example.data.HisabDatabase
import com.example.data.LocalizedStrings
import com.example.data.getLocalizedStrings
import com.example.utils.BackupManager
import com.example.utils.BackupMetadata
import com.example.utils.PdfReportGenerator
import com.example.utils.PdfReportResult
import com.example.utils.VoiceExpenseParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

enum class ScreenDestination {
    SPLASH,
    HOME,
    ANALYTICS,
    SETTINGS
}

data class HisabUiState(
    val currentScreen: ScreenDestination = ScreenDestination.SPLASH,
    val previousScreen: ScreenDestination = ScreenDestination.HOME,
    val allExpenses: List<ExpenseEntity> = emptyList(),
    val todaysExpenses: List<ExpenseEntity> = emptyList(),
    val totalSpent: Int = 5400,
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.THIS_MONTH,
    val categoryBreakdowns: List<CategoryBreakdown> = defaultReferenceBreakdowns(),
    val periodTotalSpent: Int = 5400,
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val strings: LocalizedStrings = getLocalizedStrings(AppLanguage.ENGLISH),
    val lastBackupMetadata: BackupMetadata? = null,
    val statusMessage: String? = null,
    val latestPdfResult: PdfReportResult? = null,
    val isGeneratingPdf: Boolean = false,
    val isPerformingBackup: Boolean = false
) {
    companion object {
        fun defaultReferenceBreakdowns(): List<CategoryBreakdown> = listOf(
            CategoryBreakdown(ExpenseCategory.FOOD, 2268, 42),
            CategoryBreakdown(ExpenseCategory.TRAVEL, 1512, 28),
            CategoryBreakdown(ExpenseCategory.GROCERIES, 972, 18),
            CategoryBreakdown(ExpenseCategory.OTHERS, 648, 12)
        )
    }
}

class HisabViewModel(
    application: Application,
    private val repository: ExpenseRepository
) : AndroidViewModel(application) {

    private val _currentScreen = MutableStateFlow(ScreenDestination.SPLASH)
    private val _previousScreen = MutableStateFlow(ScreenDestination.HOME)
    private val _selectedPeriod = MutableStateFlow(AnalyticsPeriod.THIS_MONTH)
    private val _lastBackupMetadata = MutableStateFlow<BackupMetadata?>(null)
    private val _statusMessage = MutableStateFlow<String?>(null)
    private val _latestPdfResult = MutableStateFlow<PdfReportResult?>(null)
    private val _isGeneratingPdf = MutableStateFlow(false)
    private val _isPerformingBackup = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            _lastBackupMetadata.value = BackupManager.getExistingBackupMetadata(getApplication())
        }
    }

    val uiState: StateFlow<HisabUiState> = combine(
        repository.allExpensesFlow,
        repository.selectedLanguageFlow,
        _currentScreen,
        _selectedPeriod,
        combine(
            _previousScreen,
            _lastBackupMetadata,
            _statusMessage,
            _latestPdfResult,
            combine(_isGeneratingPdf, _isPerformingBackup) { pdf, bkp -> pdf to bkp }
        ) { prev, bkpMeta, msg, pdfRes, flags ->
             AuxiliaryState(prev, bkpMeta, msg, pdfRes, flags.first, flags.second)
        }
    ) { allExpenses, language, screen, period, aux ->
        val thisMonthExpenses = allExpenses.filter { it.periodTag == "THIS_MONTH" }
        val totalSpentThisMonth = thisMonthExpenses.sumOf { it.amount }

        val todayItems = allExpenses
            .filter { it.isToday }
            .sortedWith(compareBy<ExpenseEntity> { it.sortOrder }.thenByDescending { it.timestamp })

        val periodExpenses = when (period) {
            AnalyticsPeriod.THIS_MONTH -> allExpenses.filter { it.periodTag == "THIS_MONTH" }
            AnalyticsPeriod.LAST_MONTH -> allExpenses.filter { it.periodTag == "LAST_MONTH" }
            AnalyticsPeriod.THIS_YEAR -> allExpenses
        }

        val periodTotal = periodExpenses.sumOf { it.amount }
        val breakdowns = computeCategoryBreakdowns(periodExpenses, periodTotal)

        HisabUiState(
            currentScreen = screen,
            previousScreen = aux.previousScreen,
            allExpenses = allExpenses,
            todaysExpenses = todayItems,
            totalSpent = totalSpentThisMonth,
            selectedPeriod = period,
            categoryBreakdowns = breakdowns,
            periodTotalSpent = periodTotal,
            selectedLanguage = language,
            strings = getLocalizedStrings(language),
            lastBackupMetadata = aux.lastBackupMetadata,
            statusMessage = aux.statusMessage,
            latestPdfResult = aux.latestPdfResult,
            isGeneratingPdf = aux.isGeneratingPdf,
            isPerformingBackup = aux.isPerformingBackup
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HisabUiState()
    )

    private data class AuxiliaryState(
        val previousScreen: ScreenDestination,
        val lastBackupMetadata: BackupMetadata?,
        val statusMessage: String?,
        val latestPdfResult: PdfReportResult?,
        val isGeneratingPdf: Boolean,
        val isPerformingBackup: Boolean
    )

    private fun computeCategoryBreakdowns(
        expenses: List<ExpenseEntity>,
        total: Int
    ): List<CategoryBreakdown> {
        val categories = listOf(
            ExpenseCategory.FOOD,
            ExpenseCategory.TRAVEL,
            ExpenseCategory.GROCERIES,
            ExpenseCategory.OTHERS
        )
        if (total <= 0) {
            return categories.map { CategoryBreakdown(it, 0, 0) }
        }

        val amounts = categories.map { cat ->
            expenses.filter {
                ExpenseCategory.fromString(it.category) == cat
            }.sumOf { it.amount }
        }

        val rawPercentages = amounts.map { amt ->
            ((amt.toDouble() / total.toDouble()) * 100.0).roundToInt()
        }.toMutableList()

        // Ensure non-zero total percentages sum cleanly to 100%
        val sumPct = rawPercentages.sum()
        if (sumPct != 100 && sumPct > 0) {
            val maxIdx = amounts.indices.maxByOrNull { amounts[it] } ?: 0
            rawPercentages[maxIdx] = (rawPercentages[maxIdx] + (100 - sumPct)).coerceAtLeast(0)
        }

        return categories.indices.map { idx ->
            CategoryBreakdown(
                category = categories[idx],
                amount = amounts[idx],
                percentage = rawPercentages[idx]
            )
        }
    }

    fun onSplashFinished() {
        if (_currentScreen.value == ScreenDestination.SPLASH) {
            _currentScreen.value = ScreenDestination.HOME
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        if (destination != _currentScreen.value) {
            if (_currentScreen.value != ScreenDestination.SPLASH) {
                _previousScreen.value = _currentScreen.value
            }
            _currentScreen.value = destination
        }
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            ScreenDestination.SETTINGS, ScreenDestination.ANALYTICS -> {
                _currentScreen.value = ScreenDestination.HOME
            }
            else -> {
                _currentScreen.value = ScreenDestination.HOME
            }
        }
    }

    fun selectAnalyticsPeriod(period: AnalyticsPeriod) {
        _selectedPeriod.value = period
    }

    fun addManualExpense(
        category: ExpenseCategory,
        description: String,
        amount: Int
    ) {
        if (amount <= 0) return
        val timeStr = SimpleDateFormat("h:mm a", Locale.US).format(Date())
        val entity = ExpenseEntity(
            category = category.displayName,
            title = category.displayName,
            description = description.ifBlank { category.displayName },
            amount = amount,
            timeFormatted = timeStr,
            dateLabel = "Today",
            periodTag = "THIS_MONTH",
            isToday = true,
            sortOrder = 0,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.addExpense(entity)
            _statusMessage.value = "Added ${category.displayName} expense: ₹$amount"
        }
    }

    fun addVoiceExpenseFromText(spokenText: String): Boolean {
        val parsed = VoiceExpenseParser.parseSpokenText(spokenText) ?: return false
        val entity = VoiceExpenseParser.toExpenseEntity(parsed)
        viewModelScope.launch {
            repository.addExpense(entity)
            _statusMessage.value = "Voice expense added: ${parsed.category.displayName} (${parsed.description}) — ₹${parsed.amount}"
        }
        return true
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _statusMessage.value = "Deleted ${expense.title} (₹${expense.amount})"
        }
    }

    fun selectLanguage(language: AppLanguage) {
        viewModelScope.launch {
            repository.setSelectedLanguage(language)
            _statusMessage.value = "Language updated to ${language.displayName}"
        }
    }

    fun generatePdfReport(context: Context) {
        if (_isGeneratingPdf.value) return
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val state = uiState.value
            val periodExpenses = when (state.selectedPeriod) {
                AnalyticsPeriod.THIS_MONTH -> state.allExpenses.filter { it.periodTag == "THIS_MONTH" }
                AnalyticsPeriod.LAST_MONTH -> state.allExpenses.filter { it.periodTag == "LAST_MONTH" }
                AnalyticsPeriod.THIS_YEAR -> state.allExpenses
            }
            val result = PdfReportGenerator.generateExpenseReportPdf(
                context = context,
                period = state.selectedPeriod,
                breakdowns = state.categoryBreakdowns,
                expenses = periodExpenses,
                totalAmount = state.periodTotalSpent
            )
            _isGeneratingPdf.value = false
            result.onSuccess { pdfRes ->
                _latestPdfResult.value = pdfRes
            }.onFailure { err ->
                _statusMessage.value = "Failed to generate PDF: ${err.message}"
            }
        }
    }

    fun dismissPdfResult() {
        _latestPdfResult.value = null
    }

    fun performDataBackup(context: Context) {
        if (_isPerformingBackup.value) return
        viewModelScope.launch {
            _isPerformingBackup.value = true
            val expenses = repository.getAllExpensesOnce()
            val result = BackupManager.performLocalBackup(context, expenses)
            _isPerformingBackup.value = false
            result.onSuccess { meta ->
                _lastBackupMetadata.value = meta
                repository.setLastBackupTime(meta.timestampFormatted)
                _statusMessage.value = "Backup saved (${meta.itemCount} expenses • ₹${meta.totalAmount})"
            }.onFailure { err ->
                _statusMessage.value = "Backup failed: ${err.message}"
            }
        }
    }

    fun restoreFromBackup(context: Context) {
        viewModelScope.launch {
            val result = BackupManager.restoreFromLocalBackup(context)
            result.onSuccess { restored ->
                repository.replaceAllExpenses(restored)
                _statusMessage.value = "Restored ${restored.size} expenses from backup"
            }.onFailure { err ->
                _statusMessage.value = err.message ?: "Restore failed"
            }
        }
    }

    fun restoreSampleReferenceData() {
        viewModelScope.launch {
            repository.restoreDefaultExpenses()
            _statusMessage.value = "Reference expense data restored"
        }
    }

    fun clearAllDataConfirmed() {
        viewModelScope.launch {
            repository.clearAllExpenses()
            _statusMessage.value = "All expense data has been deleted"
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = HisabDatabase.getInstance(application)
            val repo = ExpenseRepository(application, db.expenseDao())
            return HisabViewModel(application, repo) as T
        }
    }
}
