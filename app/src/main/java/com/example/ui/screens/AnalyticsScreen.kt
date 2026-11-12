package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnalyticsPeriod
import com.example.data.CategoryBreakdown
import com.example.data.ExpenseCategory
import com.example.ui.HisabUiState
import com.example.ui.ScreenDestination
import com.example.ui.components.HisabBottomNavigationBar
import com.example.ui.components.ThreeDPieChart
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CategoryPurple
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.RobotoFontFamily
import com.example.ui.theme.SaffronOrange
import com.example.ui.theme.SecondaryText
import com.example.utils.PdfReportGenerator

@Composable
fun AnalyticsScreen(
    uiState: HisabUiState,
    onNavigateBack: () -> Unit,
    onSelectDestination: (ScreenDestination) -> Unit,
    onSelectPeriod: (AnalyticsPeriod) -> Unit,
    onOpenCalendarDialog: () -> Unit,
    onDownloadPdfReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNavigateBack)
    val strings = uiState.strings

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryNavy, HeaderNavy)
                )
            )
            .testTag("analytics_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // 1. Dark Navy Top Bar: Back arrow, "Analytics", Calendar icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("analytics_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CardWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Text(
                    text = strings.analytics,
                    color = CardWhite,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )

                IconButton(
                    onClick = onOpenCalendarDialog,
                    modifier = Modifier.testTag("analytics_calendar_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Select Date Filter",
                        tint = CardWhite,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }

            // 2. Three Horizontal Time Filters: This Month | Last Month | This Year
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnalyticsFilterTab(
                    label = strings.thisMonth,
                    selected = uiState.selectedPeriod == AnalyticsPeriod.THIS_MONTH,
                    testTag = "filter_this_month",
                    onClick = { onSelectPeriod(AnalyticsPeriod.THIS_MONTH) }
                )
                AnalyticsFilterTab(
                    label = strings.lastMonth,
                    selected = uiState.selectedPeriod == AnalyticsPeriod.LAST_MONTH,
                    testTag = "filter_last_month",
                    onClick = { onSelectPeriod(AnalyticsPeriod.LAST_MONTH) }
                )
                AnalyticsFilterTab(
                    label = strings.thisYear,
                    selected = uiState.selectedPeriod == AnalyticsPeriod.THIS_YEAR,
                    testTag = "filter_this_year",
                    onClick = { onSelectPeriod(AnalyticsPeriod.THIS_YEAR) }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Large White Rounded Chart & Breakdown Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                        ambientColor = Color(0x33071D4B)
                    )
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(CardWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 18.dp, bottom = 96.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 3D Pie Chart
                    ThreeDPieChart(
                        breakdowns = uiState.categoryBreakdowns
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Four Expense Breakdown Rows
                    uiState.categoryBreakdowns.forEachIndexed { index, item ->
                        CategoryBreakdownRow(
                            item = item,
                            testTag = "breakdown_row_${item.category.name.lowercase()}"
                        )
                        if (index < uiState.categoryBreakdowns.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                thickness = 1.dp,
                                color = Color(0xFFEDF2FA)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Download PDF Report Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(27.dp),
                                ambientColor = PrimaryBlue,
                                spotColor = PrimaryBlue
                            )
                            .clip(RoundedCornerShape(27.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        BrightBlue,
                                        PrimaryBlue,
                                        Color(0xFF1042D8)
                                    )
                                )
                            )
                            .clickable(
                                enabled = !uiState.isGeneratingPdf,
                                onClick = onDownloadPdfReport
                            )
                            .testTag("download_pdf_report_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (uiState.isGeneratingPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = CardWhite,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = strings.downloadPdfReport,
                                    tint = CardWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(
                                text = strings.downloadPdfReport,
                                color = CardWhite,
                                fontFamily = PoppinsFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.5.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Bottom Navigation Bar (Analytics Selected)
        HisabBottomNavigationBar(
            currentScreen = ScreenDestination.ANALYTICS,
            strings = strings,
            onSelectDestination = onSelectDestination,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AnalyticsFilterTab(
    label: String,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val bgModifier = if (selected) {
        Modifier
            .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = PrimaryBlue)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BrightBlue, PrimaryBlue)
                )
            )
    } else {
        Modifier.clip(RoundedCornerShape(12.dp))
    }

    Box(
        modifier = bgModifier
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 9.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) CardWhite else Color(0xFFB5C7E8),
            fontFamily = RobotoFontFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}

@Composable
private fun CategoryBreakdownRow(
    item: CategoryBreakdown,
    testTag: String
) {
    val dotBrush = when (item.category) {
        ExpenseCategory.FOOD -> Brush.radialGradient(
            colors = listOf(Color(0xFFFFB74D), SaffronOrange, Color(0xFFD86900))
        )
        ExpenseCategory.TRAVEL -> Brush.radialGradient(
            colors = listOf(Color(0xFF64B5F6), BrightBlue, PrimaryBlue)
        )
        ExpenseCategory.GROCERIES -> Brush.radialGradient(
            colors = listOf(Color(0xFF69F0AE), IndianGreen, Color(0xFF0E9600))
        )
        ExpenseCategory.OTHERS -> Brush.radialGradient(
            colors = listOf(Color(0xFFB388FF), CategoryPurple, Color(0xFF5E21B5))
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored glossy circular indicator
        Box(
            modifier = Modifier
                .size(18.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(brush = dotBrush)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Category Name
        Text(
            text = item.category.displayName,
            color = PrimaryText,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.5.sp,
            modifier = Modifier.weight(1f)
        )

        // Amount
        Text(
            text = PdfReportGenerator.formatIndianRupee(item.amount),
            color = PrimaryText,
            fontFamily = PoppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.5.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(96.dp)
        )

        Spacer(modifier = Modifier.width(18.dp))

        // Percentage aligned to the right
        Text(
            text = "${item.percentage}%",
            color = SecondaryText,
            fontFamily = RobotoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.5.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(46.dp)
        )
    }
}
