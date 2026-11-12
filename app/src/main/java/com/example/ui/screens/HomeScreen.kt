package com.example.ui.screens

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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExpenseCategory
import com.example.data.ExpenseEntity
import com.example.ui.HisabUiState
import com.example.ui.ScreenDestination
import com.example.ui.components.HisabBottomNavigationBar
import com.example.ui.components.TotalSpentWalletIllustration
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CategoryPurple
import com.example.ui.theme.CyanAccent
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
import com.example.utils.PdfReportGenerator

@Composable
fun HomeScreen(
    uiState: HisabUiState,
    onSelectDestination: (ScreenDestination) -> Unit,
    onOpenVoiceInput: () -> Unit,
    onOpenViewAll: () -> Unit,
    onOpenMenuSheet: () -> Unit,
    onOpenNotifications: () -> Unit,
    onExpenseClicked: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = uiState.strings
    val displayExpenses = uiState.todaysExpenses.take(3)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrimaryNavy)
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // 1. Top Dark Navy Header
            HomeTopHeader(
                onMenuClick = onOpenMenuSheet,
                onNotificationClick = onOpenNotifications
            )

            // 2. Main Body Container with subtle sky/monument background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AppBackground,
                                Color(0xFFF2F7FF),
                                Color(0xFFDCEBFF)
                            )
                        )
                    )
            ) {
                // Subtle Indian monument silhouette watermark at bottom of Home body
                HomeBottomMonumentSilhouette(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                        .align(Alignment.BottomCenter)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 18.dp)
                        .padding(top = 14.dp, bottom = 104.dp)
                ) {
                    // Total Spent Card
                    TotalSpentCard(
                        totalSpentTitle = strings.totalSpent,
                        totalAmount = uiState.totalSpent
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Today's Expenses Section Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.todaysExpenses,
                            color = PrimaryText,
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onOpenViewAll)
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("view_all_expenses_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.viewAll,
                                color = PrimaryBlue,
                                fontFamily = RobotoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = strings.viewAll,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Three Expense Cards (Food, Travel, Groceries)
                    if (displayExpenses.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0x22071D4B)),
                            shape = RoundedCornerShape(20.dp),
                            color = CardWhite
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No expenses recorded today",
                                    color = PrimaryText,
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap the microphone button below to add an expense by voice",
                                    color = SecondaryText,
                                    fontFamily = RobotoFontFamily,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        displayExpenses.forEachIndexed { index, expense ->
                            ExpenseItemCard(
                                expense = expense,
                                onClick = { onExpenseClicked(expense) },
                                testTag = "expense_card_$index"
                            )
                            if (index < displayExpenses.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

        // 3. Bottom Navigation Bar
        HisabBottomNavigationBar(
            currentScreen = ScreenDestination.HOME,
            strings = strings,
            onSelectDestination = onSelectDestination,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // 4. Floating Microphone Button positioned in lower central area above bottom nav
        FloatingVoiceMicButton(
            onClick = onOpenVoiceInput,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-52).dp)
                .testTag("floating_mic_button")
        )
    }
}

@Composable
private fun HomeTopHeader(
    onMenuClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryNavy, HeaderNavy)
                )
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.testTag("home_menu_button")
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = CardWhite,
                modifier = Modifier.size(28.dp)
            )
        }

        // Center: HISAB (white) KHATA (yellow)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "HISAB ",
                color = CardWhite,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 23.sp,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "KHATA",
                color = BrandYellow,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 23.sp,
                letterSpacing = 0.5.sp
            )
        }

        // Right: Notification Bell with Red Indicator Dot
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.testTag("home_notification_button")
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = CardWhite,
                    modifier = Modifier.size(27.dp)
                )
                Box(
                    modifier = Modifier
                        .offset(x = (-2).dp, y = 2.dp)
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(DangerRed)
                        .border(1.2.dp, PrimaryNavy, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun TotalSpentCard(
    totalSpentTitle: String,
    totalAmount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = Color(0x40155BFF),
                spotColor = Color(0x40155BFF)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF8BBFFF),
                        Color(0xFF479BFF),
                        Color(0xFF6BB5FF)
                    )
                )
            )
            .padding(bottom = 5.dp) // Glossy blue bottom edge visible in reference
            .testTag("total_spent_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(25.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFC8E2FF),
                            Color(0xFFE7F3FF),
                            Color(0xFFD5EAFF)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 400f)
                    )
                )
                .border(1.5.dp, Color(0xCCFFFFFF), RoundedCornerShape(25.dp))
        ) {
            // Subtle sunburst rays in background of Total Spent card
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width * 0.65f, size.height * 0.5f)
                for (i in 0 until 8) {
                    drawLine(
                        color = Color(0x28FFFFFF),
                        start = center,
                        end = Offset(size.width * (i / 7f), 0f),
                        strokeWidth = 22f
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 22.dp, end = 12.dp, top = 16.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = totalSpentTitle,
                        color = PrimaryText,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = PdfReportGenerator.formatIndianRupee(totalAmount),
                        color = PrimaryNavy,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp,
                        lineHeight = 44.sp,
                        modifier = Modifier.testTag("total_spent_amount_text")
                    )
                }

                TotalSpentWalletIllustration(
                    modifier = Modifier
                        .width(136.dp)
                        .height(118.dp)
                )
            }
        }
    }
}

@Composable
fun ExpenseItemCard(
    expense: ExpenseEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "expense_card"
) {
    val category = ExpenseCategory.fromString(expense.category)
    val (iconGradient, iconVector) = when (category) {
        ExpenseCategory.FOOD -> Pair(
            Brush.verticalGradient(listOf(Color(0xFFFFA51F), SaffronOrange, Color(0xFFE66A00))),
            Icons.Filled.Restaurant
        )
        ExpenseCategory.TRAVEL -> Pair(
            Brush.verticalGradient(listOf(Color(0xFF47A1FF), BrightBlue, PrimaryBlue)),
            Icons.Filled.DirectionsBus
        )
        ExpenseCategory.GROCERIES -> Pair(
            Brush.verticalGradient(listOf(Color(0xFF46E619), IndianGreen, Color(0xFF0E9E00))),
            Icons.Filled.ShoppingCart
        )
        ExpenseCategory.OTHERS -> Pair(
            Brush.verticalGradient(listOf(Color(0xFFA25BFF), CategoryPurple, Color(0xFF6220C2))),
            Icons.Filled.Category
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0x20071D4B),
                spotColor = Color(0x24071D4B)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = CardWhite,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Circular Colored Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(brush = iconGradient)
                    .border(1.5.dp, Color(0x55FFFFFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = expense.title,
                    tint = CardWhite,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Middle: Title & Description
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = expense.title,
                    color = PrimaryText,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = expense.description,
                    color = SecondaryText,
                    fontFamily = RobotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.5.sp,
                    lineHeight = 17.sp
                )
            }

            // Right: Amount & Time
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = PdfReportGenerator.formatIndianRupee(expense.amount),
                    color = PrimaryText,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.5.sp,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = expense.timeFormatted,
                    color = SecondaryText,
                    fontFamily = RobotoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FloatingVoiceMicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(96.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Outer Cyan Glow Ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanAccent.copy(alpha = 0.55f),
                        BrightBlue.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = c,
                    radius = size.width / 2f
                ),
                radius = size.width / 2f,
                center = c
            )
            // Outer translucent cyan border ring
            drawCircle(
                color = CyanAccent.copy(alpha = 0.65f),
                radius = size.width * 0.41f,
                center = c
            )
        }

        // Main Royal-Blue Gradient Circular Button with White Inner Ring
        Box(
            modifier = Modifier
                .size(72.dp)
                .shadow(10.dp, CircleShape, spotColor = PrimaryBlue)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BrightBlue,
                            PrimaryBlue,
                            Color(0xFF092E96)
                        )
                    )
                )
                .border(2.dp, CyanAccent, CircleShape)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(1.5.dp, CardWhite.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Voice Expense Input",
                    tint = CardWhite,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeBottomMonumentSilhouette(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val sil = Color(0xFFC9DDF8)
        val base = h * 0.72f

        // Left dome & minaret silhouette
        drawRect(color = sil, topLeft = Offset(w * 0.08f, base - 28f), size = Size(w * 0.22f, 28f))
        drawOval(color = sil, topLeft = Offset(w * 0.14f, base - 52f), size = Size(w * 0.10f, 32f))
        drawRect(color = sil, topLeft = Offset(w * 0.05f, base - 46f), size = Size(6f, 46f))

        // Right dome & minaret silhouette
        drawRect(color = sil, topLeft = Offset(w * 0.70f, base - 28f), size = Size(w * 0.22f, 28f))
        drawOval(color = sil, topLeft = Offset(w * 0.76f, base - 52f), size = Size(w * 0.10f, 32f))
        drawRect(color = sil, topLeft = Offset(w * 0.94f, base - 46f), size = Size(6f, 46f))
    }
}
