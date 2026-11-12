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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MicWaveformBrandIcon
import com.example.ui.components.SplashBottomTricolorAndChakra
import com.example.ui.components.SplashCentralIllustration
import com.example.ui.theme.AppBackground
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PoppinsFontFamily
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.RobotoFontFamily
import com.example.ui.theme.SaffronOrange
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Starts at 0.75f (75% as shown in Reference Image 1) and completes to 100%, then navigates to Home
    var progressValue by remember { mutableFloatStateOf(0.75f) }

    LaunchedEffect(Unit) {
        delay(300)
        for (step in listOf(0.82f, 0.90f, 0.96f, 1.0f)) {
            progressValue = step
            delay(180)
        }
        onSplashFinished()
    }

    val currentPercent = (progressValue * 100f).roundToInt().coerceIn(0, 100)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .clickable { onSplashFinished() }
            .testTag("splash_screen")
    ) {
        // 1. Full-screen multi-layered curved background matching Image 1
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft sky-blue middle backdrop behind monuments
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC7E2FF),
                        Color(0xFFE7F2FF),
                        Color(0xFFF7FAFF),
                        CardWhite
                    ),
                    startY = h * 0.30f,
                    endY = h
                )
            )

            // Outer royal-blue curved wave under the dark navy header
            val royalCurvePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.39f)
                cubicTo(
                    w * 0.72f, h * 0.45f,
                    w * 0.28f, h * 0.45f,
                    0f, h * 0.35f
                )
                close()
            }
            drawPath(
                path = royalCurvePath,
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryNavy, HeaderNavy, PrimaryBlue)
                )
            )

            // Deep navy inner curved swoosh
            val deepNavyPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.24f)
                cubicTo(
                    w * 0.78f, h * 0.36f,
                    w * 0.32f, h * 0.41f,
                    0f, h * 0.29f
                )
                close()
            }
            drawPath(
                path = deepNavyPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF05153B), PrimaryNavy, HeaderNavy),
                    start = Offset(0f, 0f),
                    end = Offset(w, h * 0.38f)
                )
            )

            // Right-side Indian Tricolor curved ribbons swooping upward on the right edge
            val triStartX = w * 0.68f
            val triStartY = h * 0.305f

            val saffronRibbon = Path().apply {
                moveTo(triStartX, triStartY)
                cubicTo(w * 0.80f, h * 0.28f, w * 0.91f, h * 0.22f, w, h * 0.145f)
                lineTo(w, h * 0.195f)
                cubicTo(w * 0.91f, h * 0.255f, w * 0.80f, h * 0.295f, triStartX, triStartY + 4f)
                close()
            }
            drawPath(saffronRibbon, color = SaffronOrange)

            val whiteRibbon = Path().apply {
                moveTo(triStartX, triStartY + 4f)
                cubicTo(w * 0.80f, h * 0.295f, w * 0.91f, h * 0.255f, w, h * 0.195f)
                lineTo(w, h * 0.235f)
                cubicTo(w * 0.91f, h * 0.285f, w * 0.80f, h * 0.308f, triStartX, triStartY + 8f)
                close()
            }
            drawPath(whiteRibbon, color = CardWhite)

            val greenRibbon = Path().apply {
                moveTo(triStartX, triStartY + 8f)
                cubicTo(w * 0.80f, h * 0.308f, w * 0.91f, h * 0.285f, w, h * 0.235f)
                lineTo(w, h * 0.280f)
                cubicTo(w * 0.91f, h * 0.315f, w * 0.80f, h * 0.320f, triStartX, triStartY + 11f)
                close()
            }
            drawPath(greenRibbon, color = IndianGreen)

            // White curved pedestal / lower platform starting around 61% height
            val lowerWhiteArch = Path().apply {
                moveTo(0f, h * 0.65f)
                quadraticTo(w * 0.50f, h * 0.57f, w, h * 0.65f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = lowerWhiteArch,
                brush = Brush.verticalGradient(
                    colors = listOf(CardWhite, Color(0xFFF3F8FF)),
                    startY = h * 0.58f,
                    endY = h
                )
            )
        }

        // 2. Foreground Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Microphone & Waveform Icon
            MicWaveformBrandIcon(height = 84.dp)

            Spacer(modifier = Modifier.height(4.dp))

            // HISAB (White)
            Text(
                text = "HISAB",
                color = CardWhite,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 44.sp,
                lineHeight = 46.sp,
                letterSpacing = 1.sp
            )

            // KHATA (Yellow)
            Text(
                text = "KHATA",
                color = BrandYellow,
                fontFamily = PoppinsFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 46.sp,
                lineHeight = 48.sp,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ──── Smart Expense Tracker ────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.5.dp)
                        .background(CardWhite.copy(alpha = 0.85f))
                )
                Text(
                    text = "Smart Expense Tracker",
                    color = CardWhite,
                    fontFamily = RobotoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.5.dp)
                        .background(CardWhite.copy(alpha = 0.85f))
                )
            }

            // Central 3D Illustration (Wallet, Rupee Coins, Calendar, Clock, Receipt, Monuments)
            SplashCentralIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // Lower Section: Track • Analyze • Save
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.5.dp)
                        .background(Color(0xFFC5D5EE))
                )
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Track",
                        color = PrimaryNavy,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SaffronOrange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Analyze",
                        color = PrimaryNavy,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SaffronOrange)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save",
                        color = PrimaryNavy,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.5.dp)
                        .background(Color(0xFFC5D5EE))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizontal Rounded Progress Bar with Blue Gradient Fill + Right-aligned Percentage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .height(36.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color(0x33155BFF),
                        spotColor = Color(0x33155BFF)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFE4EEFF))
                    .border(2.dp, CardWhite, RoundedCornerShape(18.dp))
                    .padding(4.dp)
                    .testTag("splash_progress_bar"),
                contentAlignment = Alignment.CenterStart
            ) {
                // Blue gradient fill
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressValue.coerceIn(0.15f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CyanAccent,
                                    BrightBlue,
                                    PrimaryBlue
                                )
                            )
                        )
                )

                // Percentage text aligned right
                Text(
                    text = "$currentPercent%",
                    color = if (currentPercent > 88) CardWhite else PrimaryNavy,
                    fontFamily = PoppinsFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Loading... Please Wait
            Text(
                text = "Loading... Please Wait",
                color = Color(0xFF23396C),
                fontFamily = RobotoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Indian Tricolor Ribbons & Ashoka Chakra
            SplashBottomTricolorAndChakra()
        }
    }
}
