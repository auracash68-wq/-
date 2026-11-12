package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.AnalyticsPeriod
import com.example.data.AppLanguage
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.ui.screens.ExpenseItemCard
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.DangerRed
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFontFamily
import com.example.ui.theme.SaffronOrange
import com.example.ui.theme.SecondaryText
import com.example.utils.BackupMetadata
import com.example.utils.PdfReportGenerator
import com.example.utils.PdfReportResult
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceExpenseInputDialog(
    onDismiss: () -> Unit,
    onVoiceTextSubmitted: (String) -> Boolean,
    onManualExpenseSubmitted: (ExpenseCategory, String, Int) -> Unit
) {
    val context = LocalContext.current
    var spokenOrTypedPhrase by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var micStatusText by remember {
        mutableStateOf("Tap the microphone to speak or say e.g. \"Lunch at Restaurant 250\"")
    }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var descriptionInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    DisposableEffect(speechRecognizer) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                micStatusText = "Listening... Speak your expense now"
            }

            override fun onBeginningOfSpeech() {
                isListening = true
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false
                micStatusText = "Voice capture finished. You can also tap a quick phrase or enter below."
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val first = matches?.firstOrNull()
                if (!first.isNullOrBlank()) {
                    spokenOrTypedPhrase = first
                    micStatusText = "Heard: \"$first\""
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer?.setRecognitionListener(listener)
        onDispose {
            runCatching { speechRecognizer?.destroy() }
        }
    }

    fun startSpeechListening() {
        if (speechRecognizer == null) {
            micStatusText = "Speech service unavailable on this device/emulator. Use voice phrase below."
            return
        }
        runCatching {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            isListening = true
            micStatusText = "Listening... Say e.g. 'Bus Ticket 120'"
            speechRecognizer.startListening(intent)
        }.onFailure {
            isListening = false
            micStatusText = "Could not start microphone. Enter your voice command below."
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startSpeechListening()
        } else {
            micStatusText = "Microphone permission declined. You can type a voice phrase or expense below."
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("voice_input_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = CardWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Expense Entry",
                        color = PrimaryNavy,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = SecondaryText)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Microphone Trigger Circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isListening) {
                                    listOf(SaffronOrange, DangerRed)
                                } else {
                                    listOf(BrightBlue, PrimaryBlue)
                                }
                            )
                        )
                        .clickable {
                            val hasPerm = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasPerm) {
                                startSpeechListening()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        .testTag("dialog_start_mic_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Start Voice Recognition",
                        tint = CardWhite,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = micStatusText,
                    color = SecondaryText,
                    fontFamily = RobotoFontFamily,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Voice Command Chips
                val sampleCommands = listOf(
                    "Dinner at Restaurant 300",
                    "Metro Ticket 80",
                    "Fresh Fruits 150"
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleCommands.forEach { cmd ->
                        FilterChip(
                            selected = spokenOrTypedPhrase == cmd,
                            onClick = {
                                spokenOrTypedPhrase = cmd
                                errorMessage = null
                            },
                            label = {
                                Text(
                                    text = "\"$cmd\"",
                                    fontFamily = RobotoFontFamily,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = spokenOrTypedPhrase,
                    onValueChange = {
                        spokenOrTypedPhrase = it
                        errorMessage = null
                    },
                    label = { Text("Voice Command (e.g. 'Bus Ticket 120')") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_phrase_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(color = AppBackground)

                Spacer(modifier = Modifier.height(10.dp))

                // Or Direct Category + Amount Input
                Text(
                    text = "Or Enter Details Directly",
                    color = PrimaryText,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExpenseCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.displayName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlue,
                                selectedLabelColor = CardWhite
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = descriptionInput,
                        onValueChange = {
                            descriptionInput = it
                            errorMessage = null
                        },
                        label = { Text("Note") },
                        singleLine = true,
                        modifier = Modifier.weight(1.3f)
                    )
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = {
                            amountInput = it.filter { ch -> ch.isDigit() }
                            errorMessage = null
                        },
                        label = { Text("₹ Amount") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_amount_input")
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        color = DangerRed,
                        fontFamily = RobotoFontFamily,
                        fontSize = 12.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (spokenOrTypedPhrase.isNotBlank()) {
                            val ok = onVoiceTextSubmitted(spokenOrTypedPhrase)
                            if (ok) {
                                onDismiss()
                                return@Button
                            }
                        }
                        val amt = amountInput.toIntOrNull()
                        if (amt != null && amt > 0) {
                            onManualExpenseSubmitted(
                                selectedCategory,
                                descriptionInput.ifBlank { "${selectedCategory.displayName} Expense" },
                                amt
                            )
                            onDismiss()
                        } else {
                            errorMessage = "Please speak/type a phrase with an amount (e.g. 'Lunch 250') or enter ₹ Amount."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_voice_expense_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = "Add Expense",
                        color = CardWhite,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ViewAllExpensesDialog(
    expenses: List<ExpenseEntity>,
    onDismiss: () -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    onAddExpenseClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("view_all_expenses_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = AppBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "All Expenses",
                            color = PrimaryNavy,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "${expenses.size} recorded transactions",
                            color = SecondaryText,
                            fontFamily = RobotoFontFamily,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = PrimaryNavy)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ExpenseItemCard(
                                    expense = expense,
                                    onClick = {}
                                )
                            }
                            IconButton(
                                onClick = { onDeleteExpense(expense) }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete expense",
                                    tint = DangerRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Text("Close")
                    }
                    Button(
                        onClick = {
                            onDismiss()
                            onAddExpenseClick()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Expense")
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    selectedLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("language_selection_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = CardWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Choose Your Language",
                    color = PrimaryNavy,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select the preferred display language for HISAB KHATA",
                    color = SecondaryText,
                    fontFamily = RobotoFontFamily,
                    fontSize = 13.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                AppLanguage.entries.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectLanguage(lang)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .testTag("lang_option_${lang.code}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == lang,
                            onClick = {
                                onSelectLanguage(lang)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = lang.nativeName,
                                color = PrimaryText,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel", color = SecondaryText)
                }
            }
        }
    }
}

@Composable
fun DataBackupDialog(
    lastBackupMetadata: BackupMetadata?,
    isPerformingBackup: Boolean,
    onBackupNow: () -> Unit,
    onRestoreBackup: () -> Unit,
    onShareBackup: () -> Unit,
    onRestoreDefaultSampleData: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("data_backup_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = CardWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CloudDone,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Data Backup",
                        color = PrimaryNavy,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppBackground)
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    if (lastBackupMetadata != null) {
                        Column {
                            Text(
                                text = "Latest Local Backup",
                                color = PrimaryNavy,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Date: ${lastBackupMetadata.timestampFormatted}",
                                color = SecondaryText,
                                fontFamily = RobotoFontFamily,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Saved Items: ${lastBackupMetadata.itemCount} expenses (${PdfReportGenerator.formatIndianRupee(lastBackupMetadata.totalAmount)})",
                                color = SecondaryText,
                                fontFamily = RobotoFontFamily,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Text(
                            text = "No backup created yet. Tap 'Create Backup Now' to save all your expenses safely to local storage.",
                            color = SecondaryText,
                            fontFamily = RobotoFontFamily,
                            fontSize = 13.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBackupNow,
                    enabled = !isPerformingBackup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_backup_now_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPerformingBackup) "Backing up..." else "Create Backup Now",
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (lastBackupMetadata != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onRestoreBackup()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restore")
                        }

                        OutlinedButton(
                            onClick = onShareBackup,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        onRestoreDefaultSampleData()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset to Default Reference Expenses")
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close", color = SecondaryText)
                }
            }
        }
    }
}

@Composable
fun ClearAllDataConfirmationDialog(
    onConfirmClear: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("clear_data_confirm_dialog"),
        containerColor = CardWhite,
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DangerRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = null,
                    tint = DangerRed,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        title = {
            Text(
                text = "Clear All Data?",
                color = PrimaryNavy,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = "Are you sure you want to permanently delete all your saved expenses? You can backup your data first in Data Backup.",
                color = SecondaryText,
                fontFamily = RobotoFontFamily,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmClear()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("confirm_delete_all_button")
            ) {
                Text(
                    text = "Delete All Data",
                    color = CardWhite,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("cancel_delete_all_button")
            ) {
                Text("Cancel", color = PrimaryText)
            }
        }
    )
}

@Composable
fun CalendarPeriodSelectionDialog(
    selectedPeriod: AnalyticsPeriod,
    onSelectPeriod: (AnalyticsPeriod) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        title = {
            Text(
                text = "Select Analytics Period",
                color = PrimaryNavy,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AnalyticsPeriod.entries.forEach { period ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onSelectPeriod(period)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPeriod == period,
                            onClick = {
                                onSelectPeriod(period)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = period.label,
                            color = PrimaryText,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun PdfReportGeneratedDialog(
    result: PdfReportResult,
    onShareOrOpen: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("pdf_report_ready_dialog"),
        containerColor = CardWhite,
        icon = {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(IndianGreen.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = IndianGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            Text(
                text = "PDF Report Saved!",
                color = PrimaryNavy,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Your official HISAB KHATA PDF expense report has been generated.",
                    color = SecondaryText,
                    fontFamily = RobotoFontFamily,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppBackground)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = result.file.name,
                                color = PrimaryNavy,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Total: ${PdfReportGenerator.formatIndianRupee(result.totalAmount)} • ${result.itemCount} transactions",
                            color = SecondaryText,
                            fontFamily = RobotoFontFamily,
                            fontSize = 12.5.sp
                        )
                        Text(
                            text = "Location: ${result.savedDisplayPath}",
                            color = SecondaryText,
                            fontFamily = RobotoFontFamily,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onShareOrOpen()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share / Open PDF")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Done")
            }
        }
    )
}
