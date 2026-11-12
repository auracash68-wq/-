package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HisabUiState
import com.example.ui.components.MicWaveformBrandIcon
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrandYellow
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

@Composable
fun SettingsScreen(
    uiState: HisabUiState,
    onNavigateBack: () -> Unit,
    onOpenLanguageDialog: () -> Unit,
    onOpenBackupDialog: () -> Unit,
    onOpenClearDataDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)
    val strings = uiState.strings

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .testTag("settings_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // 1. Upper Dark Navy Branding Area with Curved Bottom & Indian Tricolor Stripes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(308.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Left royal blue curved accent strip
                    val leftBlueCurve = Path().apply {
                        moveTo(0f, h * 0.72f)
                        quadraticTo(w * 0.25f, h * 0.96f, w * 0.55f, h * 0.94f)
                        lineTo(w * 0.55f, h * 0.80f)
                        quadraticTo(w * 0.25f, h * 0.80f, 0f, h * 0.62f)
                        close()
                    }
                    drawPath(
                        path = leftBlueCurve,
                        brush = Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, BrightBlue)
                        )
                    )

                    // Main Dark Navy Curved Background
                    val navyHeaderPath = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, h * 0.62f)
                        cubicTo(
                            w * 0.78f, h * 0.88f,
                            w * 0.36f, h * 1.02f,
                            0f, h * 0.78f
                        )
                        close()
                    }
                    drawPath(
                        path = navyHeaderPath,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF05153B), PrimaryNavy, HeaderNavy),
                            start = Offset(0f, 0f),
                            end = Offset(w, h)
                        )
                    )

                    // Indian Tricolor Curved Decorative Stripes on the bottom-right of the header
                    val startX = w * 0.52f
                    val startY = h * 0.935f

                    val saffronCurve = Path().apply {
                        moveTo(startX, startY)
                        cubicTo(w * 0.72f, h * 0.89f, w * 0.88f, h * 0.75f, w, h * 0.58f)
                        lineTo(w, h * 0.67f)
                        cubicTo(w * 0.88f, h * 0.81f, w * 0.72f, h * 0.92f, startX, startY + 2f)
                        close()
                    }
                    drawPath(saffronCurve, color = SaffronOrange)

                    val whiteCurve = Path().apply {
                        moveTo(startX, startY + 2f)
                        cubicTo(w * 0.72f, h * 0.92f, w * 0.88f, h * 0.81f, w, h * 0.67f)
                        lineTo(w, h * 0.75f)
                        cubicTo(w * 0.88f, h * 0.86f, w * 0.72f, h * 0.945f, startX, startY + 4f)
                        close()
                    }
                    drawPath(whiteCurve, color = CardWhite)

                    val greenCurve = Path().apply {
                        moveTo(startX, startY + 4f)
                        cubicTo(w * 0.72f, h * 0.945f, w * 0.88f, h * 0.86f, w, h * 0.75f)
                        lineTo(w, h * 0.84f)
                        cubicTo(w * 0.88f, h * 0.92f, w * 0.72f, h * 0.97f, startX, startY + 6f)
                        close()
                    }
                    drawPath(greenCurve, color = IndianGreen)
                }

                // Header Foreground Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Row: Back arrow on left, "Settings" centered
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .testTag("settings_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CardWhite,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Text(
                            text = strings.settings,
                            color = CardWhite,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 23.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Microphone and Waveform Logo
                    MicWaveformBrandIcon(height = 72.dp)

                    Spacer(modifier = Modifier.height(6.dp))

                    // HISAB KHATA Branding (Single line as shown in Reference Image 3)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "HISAB ",
                            color = CardWhite,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 31.sp,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "KHATA",
                            color = BrandYellow,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 31.sp,
                            letterSpacing = 0.8.sp
                        )
                    }

                    // ──── Smart Expense Tracker ────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(CardWhite.copy(alpha = 0.75f))
                        )
                        Text(
                            text = "Smart Expense Tracker",
                            color = CardWhite,
                            fontFamily = RobotoFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(CardWhite.copy(alpha = 0.75f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Three Large White Rounded Settings Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card 1 — Language
                SettingsActionCard(
                    icon = Icons.Filled.Language,
                    iconBrush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF339BFF), BrightBlue, PrimaryBlue)
                    ),
                    title = strings.languageTitle,
                    description = strings.languageDesc,
                    testTag = "settings_language_card",
                    onClick = onOpenLanguageDialog
                )

                // Card 2 — Data Backup
                SettingsActionCard(
                    icon = Icons.Filled.CloudSync,
                    iconBrush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF339BFF), BrightBlue, PrimaryBlue)
                    ),
                    title = strings.dataBackupTitle,
                    description = strings.dataBackupDesc,
                    testTag = "settings_backup_card",
                    onClick = onOpenBackupDialog
                )

                // Card 3 — Clear All Data
                SettingsActionCard(
                    icon = Icons.Filled.Delete,
                    iconBrush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF5252), DangerRed, Color(0xFFC80E0E))
                    ),
                    title = strings.clearAllDataTitle,
                    description = strings.clearAllDataDesc,
                    testTag = "settings_clear_data_card",
                    onClick = onOpenClearDataDialog
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun SettingsActionCard(
    icon: ImageVector,
    iconBrush: Brush,
    title: String,
    description: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0x22071D4B),
                spotColor = Color(0x26071D4B)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = CardWhite,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Large circular glossy gradient icon with subtle inner white border
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(brush = iconBrush)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(1.2.dp, CardWhite.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = CardWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // Title & Description
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = PrimaryText,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.5.sp,
                    lineHeight = 23.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    color = SecondaryText,
                    fontFamily = RobotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }

            // Right-facing chevron
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PrimaryNavy,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
