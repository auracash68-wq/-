package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrandYellow
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CardWhite
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.HeaderNavy
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SaffronOrange
import kotlin.math.cos
import kotlin.math.sin

/**
 * Recreates the HISAB KHATA microphone + left green/yellow waveform + right orange/yellow waveform logo
 * seen at the top of the Splash Screen and Settings Screen.
 */
@Composable
fun MicWaveformBrandIcon(
    modifier: Modifier = Modifier,
    height: Dp = 84.dp
) {
    Canvas(modifier = modifier.height(height).fillMaxWidth(0.72f)) {
        val cx = size.width / 2f
        val cy = size.height * 0.50f
        val unit = size.height / 84f

        // Soft cyan-white glow behind microphone
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x5500D4FF), Color.Transparent),
                center = Offset(cx, cy),
                radius = 48f * unit
            ),
            radius = 48f * unit,
            center = Offset(cx, cy)
        )

        val greenYellowBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFFD4FF00), Color(0xFF16C900))
        )
        val orangeYellowBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFD400), Color(0xFFFF6D00))
        )

        // Left waveform bars (from inner to outer)
        val barWidth = 6.5f * unit
        val barCorner = CornerRadius(barWidth / 2f, barWidth / 2f)
        val leftOffsetsAndHeights = listOf(
            Pair(-27f * unit, 16f * unit),
            Pair(-38f * unit, 38f * unit),
            Pair(-50f * unit, 56f * unit),
            Pair(-62f * unit, 36f * unit),
            Pair(-74f * unit, 20f * unit),
            Pair(-85f * unit, 8f * unit)
        )
        for ((dx, barH) in leftOffsetsAndHeights) {
            drawRoundRect(
                brush = greenYellowBrush,
                topLeft = Offset(cx + dx - barWidth / 2f, cy - barH / 2f + 4f * unit),
                size = Size(barWidth, barH),
                cornerRadius = barCorner
            )
        }

        // Right waveform bars (from inner to outer)
        val rightOffsetsAndHeights = listOf(
            Pair(27f * unit, 16f * unit),
            Pair(38f * unit, 38f * unit),
            Pair(50f * unit, 56f * unit),
            Pair(62f * unit, 36f * unit),
            Pair(74f * unit, 20f * unit),
            Pair(85f * unit, 8f * unit)
        )
        for ((dx, barH) in rightOffsetsAndHeights) {
            drawRoundRect(
                brush = orangeYellowBrush,
                topLeft = Offset(cx + dx - barWidth / 2f, cy - barH / 2f + 4f * unit),
                size = Size(barWidth, barH),
                cornerRadius = barCorner
            )
        }

        // Center White Microphone Capsule
        val micW = 26f * unit
        val micH = 42f * unit
        val micTop = cy - 30f * unit
        drawRoundRect(
            color = CardWhite,
            topLeft = Offset(cx - micW / 2f, micTop),
            size = Size(micW, micH),
            cornerRadius = CornerRadius(micW / 2f, micW / 2f)
        )

        // U-shaped microphone holder arc
        val arcSize = 38f * unit
        val arcTop = cy - 22f * unit
        drawArc(
            color = CardWhite,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - arcSize / 2f, arcTop),
            size = Size(arcSize, arcSize),
            style = Stroke(width = 4.5f * unit, cap = StrokeCap.Round)
        )

        // Vertical stem & horizontal base
        val stemTop = arcTop + arcSize / 2f
        val stemBottom = stemTop + 13f * unit
        drawLine(
            color = CardWhite,
            start = Offset(cx, stemTop),
            end = Offset(cx, stemBottom),
            strokeWidth = 4.5f * unit,
            cap = StrokeCap.Round
        )
        drawLine(
            color = CardWhite,
            start = Offset(cx - 13f * unit, stemBottom),
            end = Offset(cx + 13f * unit, stemBottom),
            strokeWidth = 4.8f * unit,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Recreates the rich 3D Central Illustration on the Splash Screen (Image 1):
 * - Subtle Taj Mahal (left) & India Gate (right) architectural silhouettes and clouds
 * - Green leaves
 * - 3D Calendar on left
 * - 3D Blue Analog Clock in front-left
 * - 3D Royal Blue Stitched Wallet in center with golden clasp & Rupee coin peeking out top
 * - 3D White Receipt paper on back-right
 * - Stacked golden Indian Rupee coins on front-right
 */
@Composable
fun SplashCentralIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.52f
        val u = w / 360f

        // 1. Background Clouds & Indian Monument Silhouettes (Taj Mahal left, India Gate right)
        drawMonumentSilhouettes(w, h, u)

        // 2. Soft elliptical floor shadow beneath the 3D objects
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x38071D4B), Color(0x14071D4B), Color.Transparent),
                center = Offset(cx, h * 0.85f),
                radius = 128f * u
            ),
            topLeft = Offset(cx - 125f * u, h * 0.78f),
            size = Size(250f * u, 28f * u)
        )

        // 3. Left Green Leaves
        drawLeaf(
            anchor = Offset(cx - 88f * u, cy + 44f * u),
            tip = Offset(cx - 122f * u, cy + 8f * u),
            width = 18f * u
        )
        drawLeaf(
            anchor = Offset(cx - 82f * u, cy + 52f * u),
            tip = Offset(cx - 126f * u, cy + 40f * u),
            width = 15f * u
        )

        // 4. Right Green Leaves
        drawLeaf(
            anchor = Offset(cx + 75f * u, cy + 16f * u),
            tip = Offset(cx + 108f * u, cy - 16f * u),
            width = 18f * u
        )
        drawLeaf(
            anchor = Offset(cx + 80f * u, cy + 32f * u),
            tip = Offset(cx + 114f * u, cy + 16f * u),
            width = 16f * u
        )
        drawLeaf(
            anchor = Offset(cx + 78f * u, cy + 46f * u),
            tip = Offset(cx + 112f * u, cy + 44f * u),
            width = 14f * u
        )

        // 5. Tilted White Receipt Paper (Back-Right)
        rotate(degrees = 10f, pivot = Offset(cx + 56f * u, cy - 18f * u)) {
            val recLeft = cx + 24f * u
            val recTop = cy - 72f * u
            val recW = 58f * u
            val recH = 92f * u
            // Shadow
            drawRoundRect(
                color = Color(0x22071D4B),
                topLeft = Offset(recLeft + 3f * u, recTop + 4f * u),
                size = Size(recW, recH),
                cornerRadius = CornerRadius(6f * u, 6f * u)
            )
            // Body
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFEAF2FF))
                ),
                topLeft = Offset(recLeft, recTop),
                size = Size(recW, recH),
                cornerRadius = CornerRadius(6f * u, 6f * u)
            )
            // Receipt horizontal lines
            for (i in 0..4) {
                val ly = recTop + 16f * u + i * 12f * u
                drawLine(
                    color = Color(0xFFB5C7E6),
                    start = Offset(recLeft + 10f * u, ly),
                    end = Offset(recLeft + recW - 10f * u, ly),
                    strokeWidth = 3.5f * u,
                    cap = StrokeCap.Round
                )
            }
        }

        // 6. Tilted 3D Calendar (Left)
        rotate(degrees = -10f, pivot = Offset(cx - 68f * u, cy + 6f * u)) {
            val calLeft = cx - 98f * u
            val calTop = cy - 34f * u
            val calW = 62f * u
            val calH = 74f * u

            // 3D depth back
            drawRoundRect(
                color = Color(0xFFBBD2F6),
                topLeft = Offset(calLeft - 4f * u, calTop + 4f * u),
                size = Size(calW, calH),
                cornerRadius = CornerRadius(10f * u, 10f * u)
            )
            // Main white card
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(calLeft, calTop),
                size = Size(calW, calH),
                cornerRadius = CornerRadius(10f * u, 10f * u)
            )
            // Blue header bar
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryBlue, BrightBlue)
                ),
                topLeft = Offset(calLeft, calTop),
                size = Size(calW, 18f * u),
                cornerRadius = CornerRadius(10f * u, 10f * u)
            )
            // Calendar binder rings
            drawRoundRect(
                color = BrightBlue,
                topLeft = Offset(calLeft + 14f * u, calTop - 6f * u),
                size = Size(6f * u, 12f * u),
                cornerRadius = CornerRadius(3f * u, 3f * u)
            )
            drawRoundRect(
                color = BrightBlue,
                topLeft = Offset(calLeft + calW - 20f * u, calTop - 6f * u),
                size = Size(6f * u, 12f * u),
                cornerRadius = CornerRadius(3f * u, 3f * u)
            )
            // 3x3 Calendar Grid Squares
            for (r in 0..2) {
                for (c in 0..2) {
                    val gx = calLeft + 9f * u + c * 16f * u
                    val gy = calTop + 25f * u + r * 14f * u
                    drawRoundRect(
                        color = Color(0xFFB5CEF5),
                        topLeft = Offset(gx, gy),
                        size = Size(11f * u, 9f * u),
                        cornerRadius = CornerRadius(2.5f * u, 2.5f * u)
                    )
                }
            }
        }

        // 7. Golden Rupee Coin Peeking Out of Top of Wallet
        drawRupeeCoin(
            center = Offset(cx + 8f * u, cy - 48f * u),
            radius = 22f * u,
            u = u
        )

        // 8. Center 3D Royal Blue Wallet
        val walLeft = cx - 42f * u
        val walTop = cy - 38f * u
        val walW = 92f * u
        val walH = 82f * u

        // Back wallet flap
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0A39B0), Color(0xFF06257A))
            ),
            topLeft = Offset(walLeft + 4f * u, walTop - 8f * u),
            size = Size(walW - 4f * u, walH),
            cornerRadius = CornerRadius(14f * u, 14f * u)
        )

        // Front wallet body
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF1E75FF), Color(0xFF0E47CC)),
                start = Offset(walLeft, walTop),
                end = Offset(walLeft + walW, walTop + walH)
            ),
            topLeft = Offset(walLeft, walTop),
            size = Size(walW, walH),
            cornerRadius = CornerRadius(14f * u, 14f * u)
        )

        // Dashed white stitching inside wallet rim
        drawRoundRect(
            color = Color(0xCCFFFFFF),
            topLeft = Offset(walLeft + 6f * u, walTop + 6f * u),
            size = Size(walW - 12f * u, walH - 12f * u),
            cornerRadius = CornerRadius(10f * u, 10f * u),
            style = Stroke(
                width = 1.8f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f * u, 4f * u), 0f)
            )
        )

        // Wallet Clasp Strap on right
        val strapW = 30f * u
        val strapH = 22f * u
        val strapLeft = walLeft + walW - 24f * u
        val strapTop = walTop + walH * 0.36f
        drawRoundRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF155BFF), Color(0xFF09359E))
            ),
            topLeft = Offset(strapLeft, strapTop),
            size = Size(strapW, strapH),
            cornerRadius = CornerRadius(10f * u, 10f * u)
        )
        // Golden clasp button
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BrandYellow, SaffronOrange),
                center = Offset(strapLeft + 11f * u, strapTop + strapH / 2f),
                radius = 6.5f * u
            ),
            radius = 6f * u,
            center = Offset(strapLeft + 11f * u, strapTop + strapH / 2f)
        )

        // 9. Front-Left 3D Blue Analog Clock
        val clkCenter = Offset(cx - 48f * u, cy + 20f * u)
        val clkRadius = 26f * u
        // Drop shadow
        drawCircle(
            color = Color(0x33071D4B),
            radius = clkRadius + 2f * u,
            center = Offset(clkCenter.x + 2f * u, clkCenter.y + 4f * u)
        )
        // Outer blue casing
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(BrightBlue, Color(0xFF0A42B8)),
                start = Offset(clkCenter.x - clkRadius, clkCenter.y - clkRadius),
                end = Offset(clkCenter.x + clkRadius, clkCenter.y + clkRadius)
            ),
            radius = clkRadius,
            center = clkCenter
        )
        // Inner white clock face
        drawCircle(
            color = Color.White,
            radius = clkRadius * 0.76f,
            center = clkCenter
        )
        // Hour ticks
        for (i in 0 until 12) {
            val angleRad = Math.toRadians((i * 30).toDouble())
            val rIn = clkRadius * 0.60f
            val rOut = clkRadius * 0.68f
            drawLine(
                color = SecondaryNavyTick,
                start = Offset(
                    clkCenter.x + (rIn * cos(angleRad)).toFloat(),
                    clkCenter.y + (rIn * sin(angleRad)).toFloat()
                ),
                end = Offset(
                    clkCenter.x + (rOut * cos(angleRad)).toFloat(),
                    clkCenter.y + (rOut * sin(angleRad)).toFloat()
                ),
                strokeWidth = 1.5f * u,
                cap = StrokeCap.Round
            )
        }
        // Clock hands (12:00 and 2:00 as in Reference Image 1)
        drawLine(
            color = PrimaryNavy,
            start = clkCenter,
            end = Offset(clkCenter.x, clkCenter.y - clkRadius * 0.48f),
            strokeWidth = 3f * u,
            cap = StrokeCap.Round
        )
        drawLine(
            color = PrimaryNavy,
            start = clkCenter,
            end = Offset(clkCenter.x + clkRadius * 0.38f, clkCenter.y - clkRadius * 0.22f),
            strokeWidth = 3f * u,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = BrightBlue,
            radius = 2.8f * u,
            center = clkCenter
        )

        // 10. Front-Right Stacked Golden Coins + Front Rupee Coin
        val stackX = cx + 64f * u
        val stackBottomY = cy + 44f * u
        for (idx in 0..3) {
            val coinY = stackBottomY - idx * 9.5f * u
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFD98200), BrandYellow, Color(0xFFE08A00))
                ),
                topLeft = Offset(stackX - 18f * u, coinY),
                size = Size(38f * u, 8.5f * u),
                cornerRadius = CornerRadius(4f * u, 4f * u)
            )
        }
        // Front-facing Golden Rupee Coin
        drawRupeeCoin(
            center = Offset(cx + 38f * u, cy + 34f * u),
            radius = 23f * u,
            u = u
        )
    }
}

private val SecondaryNavyTick = Color(0xFF53658D)

private fun DrawScope.drawRupeeCoin(center: Offset, radius: Float, u: Float) {
    // Shadow
    drawCircle(
        color = Color(0x33071D4B),
        radius = radius + 1.5f * u,
        center = Offset(center.x + 1.5f * u, center.y + 3f * u)
    )
    // Outer gold rim
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFE54C), SaffronOrange),
            start = Offset(center.x - radius, center.y - radius),
            end = Offset(center.x + radius, center.y + radius)
        ),
        radius = radius,
        center = center
    )
    // Inner recessed gold face
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFD926), Color(0xFFE58900)),
            center = center,
            radius = radius * 0.78f
        ),
        radius = radius * 0.78f,
        center = center
    )
    // Rupee symbol
    drawContext.canvas.nativeCanvas.apply {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.parseColor("#B25900")
            textSize = radius * 1.05f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val fontMetrics = paint.fontMetrics
        val textOffset = (fontMetrics.descent + fontMetrics.ascent) / 2f
        drawText("₹", center.x, center.y - textOffset, paint)
    }
}

private fun DrawScope.drawLeaf(anchor: Offset, tip: Offset, width: Float) {
    val path = Path().apply {
        moveTo(anchor.x, anchor.y)
        val midX = (anchor.x + tip.x) / 2f
        val midY = (anchor.y + tip.y) / 2f
        quadraticTo(midX - width, midY - width * 0.6f, tip.x, tip.y)
        quadraticTo(midX + width * 0.6f, midY + width, anchor.x, anchor.y)
        close()
    }
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF4CE800), Color(0xFF0E9600)),
            start = tip,
            end = anchor
        )
    )
}

private fun DrawScope.drawMonumentSilhouettes(w: Float, h: Float, u: Float) {
    val silColor = Color(0xFFC4DCFA)
    val baseLine = h * 0.66f

    // Left Taj Mahal Silhouette
    val tajX = w * 0.14f
    // Main base
    drawRect(
        color = silColor,
        topLeft = Offset(tajX - 36f * u, baseLine - 28f * u),
        size = Size(72f * u, 28f * u)
    )
    // Center dome
    drawOval(
        color = silColor,
        topLeft = Offset(tajX - 16f * u, baseLine - 52f * u),
        size = Size(32f * u, 30f * u)
    )
    // Spire
    drawLine(
        color = silColor,
        start = Offset(tajX, baseLine - 62f * u),
        end = Offset(tajX, baseLine - 50f * u),
        strokeWidth = 2f * u
    )
    // Left & Right Minarets
    drawRect(
        color = silColor,
        topLeft = Offset(tajX - 46f * u, baseLine - 44f * u),
        size = Size(5f * u, 44f * u)
    )
    drawRect(
        color = silColor,
        topLeft = Offset(tajX + 41f * u, baseLine - 44f * u),
        size = Size(5f * u, 44f * u)
    )

    // Right India Gate Silhouette
    val gateX = w * 0.86f
    drawRect(
        color = silColor,
        topLeft = Offset(gateX - 28f * u, baseLine - 52f * u),
        size = Size(56f * u, 52f * u)
    )
    drawRect(
        color = silColor,
        topLeft = Offset(gateX - 20f * u, baseLine - 60f * u),
        size = Size(40f * u, 8f * u)
    )
    // Inner arch cutout
    drawArc(
        color = Color(0xFFEAF3FF),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(gateX - 12f * u, baseLine - 36f * u),
        size = Size(24f * u, 24f * u)
    )
    drawRect(
        color = Color(0xFFEAF3FF),
        topLeft = Offset(gateX - 12f * u, baseLine - 24f * u),
        size = Size(24f * u, 24f * u)
    )
}

/**
 * Recreates the wallet + golden rupee coins + upward bar chart illustration
 * inside the right side of the Total Spent card on the Home Screen (Image 2, left phone).
 */
@Composable
fun TotalSpentWalletIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val u = w / 135f

        // 1. Upward Bar Chart & Trend Arrow on the bottom-left of wallet
        val barColor = Color(0xFF6CC2FF)
        val barBaseY = h * 0.88f
        val barWidths = 6f * u
        val barHeights = listOf(12f * u, 20f * u, 28f * u, 36f * u)
        barHeights.forEachIndexed { idx, bh ->
            val bx = 6f * u + idx * 10f * u
            drawRoundRect(
                color = barColor,
                topLeft = Offset(bx, barBaseY - bh),
                size = Size(barWidths, bh),
                cornerRadius = CornerRadius(2f * u, 2f * u)
            )
        }
        // Upward trend arrow
        val arrowPath = Path().apply {
            moveTo(4f * u, barBaseY - 14f * u)
            quadraticTo(18f * u, barBaseY - 22f * u, 36f * u, barBaseY - 44f * u)
        }
        drawPath(
            path = arrowPath,
            color = Color(0xFF2FA2FF),
            style = Stroke(width = 2.6f * u, cap = StrokeCap.Round)
        )
        // Arrow head
        drawLine(
            color = Color(0xFF2FA2FF),
            start = Offset(30f * u, barBaseY - 43f * u),
            end = Offset(36f * u, barBaseY - 44f * u),
            strokeWidth = 2.6f * u,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color(0xFF2FA2FF),
            start = Offset(36f * u, barBaseY - 37f * u),
            end = Offset(36f * u, barBaseY - 44f * u),
            strokeWidth = 2.6f * u,
            cap = StrokeCap.Round
        )

        // 2. Golden Rupee Coin peeking out the top of the wallet
        drawRupeeCoin(
            center = Offset(w * 0.62f, h * 0.22f),
            radius = 15f * u,
            u = u
        )

        // 3. Royal Blue 3D Wallet
        val walLeft = w * 0.30f
        val walTop = h * 0.25f
        val walW = 68f * u
        val walH = 56f * u

        // Back flap
        drawRoundRect(
            color = Color(0xFF0A39B0),
            topLeft = Offset(walLeft + 3f * u, walTop - 5f * u),
            size = Size(walW - 4f * u, walH),
            cornerRadius = CornerRadius(10f * u, 10f * u)
        )
        // Main wallet body
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF2176FF), Color(0xFF0E43C7)),
                start = Offset(walLeft, walTop),
                end = Offset(walLeft + walW, walTop + walH)
            ),
            topLeft = Offset(walLeft, walTop),
            size = Size(walW, walH),
            cornerRadius = CornerRadius(10f * u, 10f * u)
        )
        // White dashed stitching
        drawRoundRect(
            color = Color(0xCCFFFFFF),
            topLeft = Offset(walLeft + 5f * u, walTop + 5f * u),
            size = Size(walW - 10f * u, walH - 10f * u),
            cornerRadius = CornerRadius(7f * u, 7f * u),
            style = Stroke(
                width = 1.4f * u,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f * u, 3f * u), 0f)
            )
        )
        // Clasp strap
        val strapLeft = walLeft + walW - 18f * u
        val strapTop = walTop + walH * 0.36f
        drawRoundRect(
            color = Color(0xFF0A39B0),
            topLeft = Offset(strapLeft, strapTop),
            size = Size(22f * u, 16f * u),
            cornerRadius = CornerRadius(7f * u, 7f * u)
        )
        drawCircle(
            color = BrandYellow,
            radius = 4.5f * u,
            center = Offset(strapLeft + 8f * u, strapTop + 8f * u)
        )

        // 4. Stacked Golden Rupee Coins in front-right
        drawRupeeCoin(
            center = Offset(w * 0.81f, h * 0.74f),
            radius = 12f * u,
            u = u
        )
        drawRupeeCoin(
            center = Offset(w * 0.68f, h * 0.80f),
            radius = 13.5f * u,
            u = u
        )
    }
}

/**
 * Recreates the Indian Tricolor Bottom Curved Stripes + Ashoka Chakra at the bottom of the Splash Screen.
 */
@Composable
fun SplashBottomTricolorAndChakra(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Left Tricolor Curves
            val saffronLeft = Path().apply {
                moveTo(0f, h * 0.15f)
                cubicTo(w * 0.18f, -h * 0.10f, w * 0.34f, h * 0.45f, w * 0.50f, h * 0.62f)
                lineTo(w * 0.50f, h * 0.78f)
                cubicTo(w * 0.34f, h * 0.60f, w * 0.18f, h * 0.18f, 0f, h * 0.42f)
                close()
            }
            drawPath(saffronLeft, color = SaffronOrange)

            val whiteLeft = Path().apply {
                moveTo(0f, h * 0.40f)
                cubicTo(w * 0.18f, h * 0.16f, w * 0.34f, h * 0.58f, w * 0.50f, h * 0.72f)
                lineTo(w * 0.50f, h * 0.86f)
                cubicTo(w * 0.34f, h * 0.72f, w * 0.18f, h * 0.40f, 0f, h * 0.65f)
                close()
            }
            drawPath(whiteLeft, color = CardWhite)

            val greenLeft = Path().apply {
                moveTo(0f, h * 0.62f)
                cubicTo(w * 0.18f, h * 0.38f, w * 0.34f, h * 0.72f, w * 0.50f, h * 0.84f)
                lineTo(w * 0.50f, h * 0.98f)
                cubicTo(w * 0.34f, h * 0.88f, w * 0.18f, h * 0.66f, 0f, h * 0.90f)
                close()
            }
            drawPath(greenLeft, color = IndianGreen)

            // Right Tricolor Curves
            val saffronRight = Path().apply {
                moveTo(w * 0.50f, h * 0.62f)
                cubicTo(w * 0.66f, h * 0.72f, w * 0.82f, h * 0.42f, w, h * 0.02f)
                lineTo(w, h * 0.32f)
                cubicTo(w * 0.82f, h * 0.62f, w * 0.66f, h * 0.84f, w * 0.50f, h * 0.76f)
                close()
            }
            drawPath(saffronRight, color = SaffronOrange)

            val whiteRight = Path().apply {
                moveTo(w * 0.50f, h * 0.74f)
                cubicTo(w * 0.66f, h * 0.82f, w * 0.82f, h * 0.60f, w, h * 0.30f)
                lineTo(w, h * 0.56f)
                cubicTo(w * 0.82f, h * 0.78f, w * 0.66f, h * 0.92f, w * 0.50f, h * 0.86f)
                close()
            }
            drawPath(whiteRight, color = CardWhite)

            val greenRight = Path().apply {
                moveTo(w * 0.50f, h * 0.84f)
                cubicTo(w * 0.66f, h * 0.92f, w * 0.82f, h * 0.76f, w, h * 0.54f)
                lineTo(w, h * 0.82f)
                cubicTo(w * 0.82f, h * 0.96f, w * 0.66f, h * 1.02f, w * 0.50f, h * 0.98f)
                close()
            }
            drawPath(greenRight, color = IndianGreen)
        }

        // Center Ashoka Chakra Badge
        Canvas(modifier = Modifier.size(64.dp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.width * 0.42f

            // White outer circle badge
            drawCircle(
                color = CardWhite,
                radius = size.width * 0.49f,
                center = Offset(cx, cy)
            )
            // Outer navy rim of Ashoka Chakra
            val chakraColor = Color(0xFF0A2878)
            drawCircle(
                color = chakraColor,
                radius = r,
                center = Offset(cx, cy),
                style = Stroke(width = size.width * 0.055f)
            )
            // Center navy hub
            drawCircle(
                color = chakraColor,
                radius = r * 0.18f,
                center = Offset(cx, cy)
            )
            // 24 spokes
            for (i in 0 until 24) {
                val angle = Math.toRadians((i * 15).toDouble())
                val ex = cx + (r * cos(angle)).toFloat()
                val ey = cy + (r * sin(angle)).toFloat()
                drawLine(
                    color = chakraColor,
                    start = Offset(cx, cy),
                    end = Offset(ex, ey),
                    strokeWidth = size.width * 0.022f
                )
            }
        }
    }
}
