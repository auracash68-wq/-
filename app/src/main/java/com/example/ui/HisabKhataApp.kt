package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CalendarPeriodSelectionDialog
import com.example.ui.components.ClearAllDataConfirmationDialog
import com.example.ui.components.DataBackupDialog
import com.example.ui.components.LanguageSelectionDialog
import com.example.ui.components.PdfReportGeneratedDialog
import com.example.ui.components.ViewAllExpensesDialog
import com.example.ui.components.VoiceExpenseInputDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CardWhite
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.RobotoFontFamily
import com.example.ui.theme.SecondaryText
import com.example.utils.BackupManager
import com.example.utils.PdfReportGenerator

@Composable
fun HisabKhataApp(
    viewModel: HisabViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showVoiceDialog by remember { mutableStateOf(false) }
    var showViewAllDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showMenuDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.currentScreen) {
            ScreenDestination.SPLASH -> {
                SplashScreen(
                    onSplashFinished = { viewModel.onSplashFinished() }
                )
            }

            ScreenDestination.HOME -> {
                HomeScreen(
                    uiState = uiState,
                    onSelectDestination = { dest -> viewModel.navigateTo(dest) },
                    onOpenVoiceInput = { showVoiceDialog = true },
                    onOpenViewAll = { showViewAllDialog = true },
                    onOpenMenuSheet = { showMenuDialog = true },
                    onOpenNotifications = { showNotificationsDialog = true },
                    onExpenseClicked = { showViewAllDialog = true }
                )
            }

            ScreenDestination.ANALYTICS -> {
                AnalyticsScreen(
                    uiState = uiState,
                    onNavigateBack = { viewModel.navigateBack() },
                    onSelectDestination = { dest -> viewModel.navigateTo(dest) },
                    onSelectPeriod = { period -> viewModel.selectAnalyticsPeriod(period) },
                    onOpenCalendarDialog = { showCalendarDialog = true },
                    onDownloadPdfReport = { viewModel.generatePdfReport(context) }
                )
            }

            ScreenDestination.SETTINGS -> {
                SettingsScreen(
                    uiState = uiState,
                    onNavigateBack = { viewModel.navigateBack() },
                    onOpenLanguageDialog = { showLanguageDialog = true },
                    onOpenBackupDialog = { showBackupDialog = true },
                    onOpenClearDataDialog = { showClearDataDialog = true }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = PrimaryNavy,
                contentColor = CardWhite,
                shape = RoundedCornerShape(14.dp)
            )
        }
    }

    // Interactive Dialogs & Sheets
    if (showVoiceDialog) {
        VoiceExpenseInputDialog(
            onDismiss = { showVoiceDialog = false },
            onVoiceTextSubmitted = { phrase -> viewModel.addVoiceExpenseFromText(phrase) },
            onManualExpenseSubmitted = { cat, desc, amt ->
                viewModel.addManualExpense(cat, desc, amt)
            }
        )
    }

    if (showViewAllDialog) {
        ViewAllExpensesDialog(
            expenses = uiState.allExpenses,
            onDismiss = { showViewAllDialog = false },
            onDeleteExpense = { expense -> viewModel.deleteExpense(expense) },
            onAddExpenseClick = { showVoiceDialog = true }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            selectedLanguage = uiState.selectedLanguage,
            onSelectLanguage = { lang -> viewModel.selectLanguage(lang) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showBackupDialog) {
        DataBackupDialog(
            lastBackupMetadata = uiState.lastBackupMetadata,
            isPerformingBackup = uiState.isPerformingBackup,
            onBackupNow = { viewModel.performDataBackup(context) },
            onRestoreBackup = { viewModel.restoreFromBackup(context) },
            onShareBackup = { BackupManager.shareBackupFile(context) },
            onRestoreDefaultSampleData = { viewModel.restoreSampleReferenceData() },
            onDismiss = { showBackupDialog = false }
        )
    }

    if (showClearDataDialog) {
        ClearAllDataConfirmationDialog(
            onConfirmClear = { viewModel.clearAllDataConfirmed() },
            onDismiss = { showClearDataDialog = false }
        )
    }

    if (showCalendarDialog) {
        CalendarPeriodSelectionDialog(
            selectedPeriod = uiState.selectedPeriod,
            onSelectPeriod = { period -> viewModel.selectAnalyticsPeriod(period) },
            onDismiss = { showCalendarDialog = false }
        )
    }

    if (uiState.latestPdfResult != null) {
        PdfReportGeneratedDialog(
            result = uiState.latestPdfResult!!,
            onShareOrOpen = {
                PdfReportGenerator.shareOrOpenPdf(context, uiState.latestPdfResult!!.file)
            },
            onDismiss = { viewModel.dismissPdfResult() }
        )
    }

    if (showMenuDialog) {
        AlertDialog(
            onDismissRequest = { showMenuDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "HISAB KHATA Menu",
                    color = PrimaryNavy,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Smart Expense Tracker • Quick Navigation",
                        color = SecondaryText,
                        fontFamily = RobotoFontFamily,
                        fontSize = 13.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            showMenuDialog = false
                            viewModel.navigateTo(ScreenDestination.ANALYTICS)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Open Analytics")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showMenuDialog = false
                            viewModel.navigateTo(ScreenDestination.SETTINGS)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Open Settings")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            showMenuDialog = false
                            viewModel.navigateTo(ScreenDestination.SPLASH)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("View Splash Screen", color = PrimaryNavy)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMenuDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showNotificationsDialog) {
        val todayTotal = uiState.todaysExpenses.sumOf { it.amount }
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            containerColor = CardWhite,
            title = {
                Text(
                    text = "Expense Summary",
                    color = PrimaryNavy,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "• Today’s Expenses: ${uiState.todaysExpenses.size} items (${PdfReportGenerator.formatIndianRupee(todayTotal)})",
                        color = PrimaryNavy,
                        fontFamily = RobotoFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• This Month Total Spent: ${PdfReportGenerator.formatIndianRupee(uiState.totalSpent)}",
                        color = SecondaryText,
                        fontFamily = RobotoFontFamily,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
