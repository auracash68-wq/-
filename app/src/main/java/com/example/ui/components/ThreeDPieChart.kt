package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.CategoryBreakdown
import com.example.data.ExpenseCategory
import com.example.ui.theme.BrightBlue
import com.example.ui.theme.CategoryPurple
import com.example.ui.theme.IndianGreen
import com.example.ui.theme.SaffronOrange
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ThreeDPieChart(
    breakdowns: List<CategoryBreakdown>,
    modifier: Modifier = Modifier,
    onSliceSelected: (ExpenseCategory?) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500),
        label = "pie_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(235.dp)
            .testTag("analytics_3d_pie_chart"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(235.dp)
                .pointerInput(breakdowns) {
                    detectTapGestures { tapOffset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        val rx = (w * 0.76f) / 2f
                        val ry = rx * 0.78f
                        val cx = w / 2f
                        val cy = h * 0.45f

                        val dx = (tapOffset.x - cx) / rx
                        val dy = (tapOffset.y - cy) / ry
                        if (dx * dx + dy * dy <= 1.15f) {
                            var deg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (deg < -90f) deg += 360f
                            var startAngle = -90f
                            val totalPct = breakdowns.sumOf { it.percentage }.coerceAtLeast(1)
                            for (item in breakdowns) {
                                val sweep = (item.percentage.toFloat() / totalPct.toFloat()) * 360f
                                if (deg >= startAngle && deg <= startAngle + sweep) {
                                    selectedCategory = if (selectedCategory == item.category) null else item.category
                                    onSliceSelected(selectedCategory)
                                    break
                                }
                                startAngle += sweep
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val chartW = (w * 0.78f).coerceAtMost(h * 1.12f)
            val chartH = chartW * 0.78f
            val depth = chartH * 0.145f

            val left = (w - chartW) / 2f
            val top = (h - chartH - depth) / 2f - 4f
            val cx = left + chartW / 2f
            val cy = top + chartH / 2f
            val rx = chartW / 2f
            val ry = chartH / 2f

            // 1. Soft drop shadow underneath the 3D cylinder
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x40071D4B), Color(0x18071D4B), Color.Transparent),
                    center = Offset(cx, cy + depth + 12f),
                    radius = rx * 1.08f
                ),
                topLeft = Offset(left - 10f, top + depth + 6f),
                size = Size(chartW + 20f, chartH * 0.96f)
            )

            val totalPct = breakdowns.sumOf { it.percentage }
            if (totalPct <= 0) {
                // Empty state 3D cylinder
                drawOval(
                    color = Color(0xFFBAC8E0),
                    topLeft = Offset(left, top + depth),
                    size = Size(chartW, chartH)
                )
                drawOval(
                    color = Color(0xFFDCE6F7),
                    topLeft = Offset(left, top),
                    size = Size(chartW, chartH)
                )
                return@Canvas
            }

            // Prepare slice angles starting at -90 degrees (top vertical) matching Reference Image 2
            data class SliceRenderData(
                val item: CategoryBreakdown,
                val startDeg: Float,
                val sweepDeg: Float,
                val topLightColor: Color,
                val topBaseColor: Color,
                val sideDarkColor: Color
            )

            var currentAngle = -90f
            val slices = breakdowns.map { item ->
                val sweep = (item.percentage.toFloat() / totalPct.toFloat()) * 360f * animProgress
                val colors = when (item.category) {
                    ExpenseCategory.FOOD -> Triple(
                        Color(0xFFFFA726),
                        SaffronOrange,
                        Color(0xFFD16000)
                    )
                    ExpenseCategory.TRAVEL -> Triple(
                        Color(0xFF3D9BFF),
                        BrightBlue,
                        Color(0xFF0051C4)
                    )
                    ExpenseCategory.GROCERIES -> Triple(
                        Color(0xFF4CE820),
                        IndianGreen,
                        Color(0xFF0D8A00)
                    )
                    ExpenseCategory.OTHERS -> Triple(
                        Color(0xFFA25BFF),
                        CategoryPurple,
                        Color(0xFF5A1FB8)
                    )
                }
                val slice = SliceRenderData(
                    item = item,
                    startDeg = currentAngle,
                    sweepDeg = sweep,
                    topLightColor = colors.first,
                    topBaseColor = colors.second,
                    sideDarkColor = colors.third
                )
                currentAngle += sweep
                slice
            }

            // 2. Draw 3D Extruded Side Walls for each slice
            // Using polygonal strips along the arc so the vertical extrusion wall is completely solid and crisp
            for (slice in slices) {
                if (slice.sweepDeg <= 0.5f) continue
                val midAngleRad = Math.toRadians((slice.startDeg + slice.sweepDeg / 2f).toDouble())
                val pullOut = if (selectedCategory == slice.item.category) 8f else 0f
                val ox = (cos(midAngleRad) * pullOut).toFloat()
                val oy = (sin(midAngleRad) * pullOut).toFloat()

                val steps = (slice.sweepDeg.toInt()).coerceAtLeast(16)
                val sidePath = Path()
                for (i in 0..steps) {
                    val deg = slice.startDeg + (slice.sweepDeg * i / steps.toFloat())
                    val rad = Math.toRadians(deg.toDouble())
                    val px = cx + ox + (rx * cos(rad)).toFloat()
                    val py = cy + oy + (ry * sin(rad)).toFloat()
                    if (i == 0) {
                        sidePath.moveTo(px, py)
                    } else {
                        sidePath.lineTo(px, py)
                    }
                }
                for (i in steps downTo 0) {
                    val deg = slice.startDeg + (slice.sweepDeg * i / steps.toFloat())
                    val rad = Math.toRadians(deg.toDouble())
                    val px = cx + ox + (rx * cos(rad)).toFloat()
                    val py = cy + oy + depth + (ry * sin(rad)).toFloat()
                    sidePath.lineTo(px, py)
                }
                sidePath.close()

                drawPath(
                    path = sidePath,
                    color = slice.sideDarkColor
                )
            }

            // 3. Draw Top Elliptical Pie Slices + Percentage Labels
            for (slice in slices) {
                if (slice.sweepDeg <= 0.5f) continue
                val midDeg = slice.startDeg + slice.sweepDeg / 2f
                val midRad = Math.toRadians(midDeg.toDouble())
                val pullOut = if (selectedCategory == slice.item.category) 8f else 0f
                val ox = (cos(midRad) * pullOut).toFloat()
                val oy = (sin(midRad) * pullOut).toFloat()

                drawArc(
                    brush = Brush.radialGradient(
                        colors = listOf(slice.topLightColor, slice.topBaseColor),
                        center = Offset(cx + ox - rx * 0.15f, cy + oy - ry * 0.15f),
                        radius = rx * 1.1f
                    ),
                    startAngle = slice.startDeg,
                    sweepAngle = slice.sweepDeg,
                    useCenter = true,
                    topLeft = Offset(left + ox, top + oy),
                    size = Size(chartW, chartH)
                )

                // Subtle 3D rim highlight & slice divider
                drawArc(
                    color = Color(0x44071D4B),
                    startAngle = slice.startDeg,
                    sweepAngle = slice.sweepDeg,
                    useCenter = true,
                    topLeft = Offset(left + ox, top + oy),
                    size = Size(chartW, chartH),
                    style = Stroke(width = 1.8f)
                )
            }

            // 4. Draw Crisp White Percentage Labels inside each slice
            for (slice in slices) {
                if (slice.item.percentage <= 0) continue
                val midDeg = slice.startDeg + slice.sweepDeg / 2f
                val midRad = Math.toRadians(midDeg.toDouble())
                val pullOut = if (selectedCategory == slice.item.category) 8f else 0f
                val labelRadiusFactor = if (slice.item.percentage < 15) 0.64f else 0.56f
                val lx = cx + (cos(midRad) * (rx * labelRadiusFactor + pullOut)).toFloat()
                val ly = cy + (sin(midRad) * (ry * labelRadiusFactor + pullOut)).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        textSize = if (slice.item.percentage < 15) chartW * 0.072f else chartW * 0.086f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        textAlign = Paint.Align.CENTER
                        setShadowLayer(4f, 0f, 2f, android.graphics.Color.argb(95, 7, 29, 75))
                    }
                    val fm = textPaint.fontMetrics
                    val textOffset = (fm.descent + fm.ascent) / 2f
                    drawText("${slice.item.percentage}%", lx, ly - textOffset, textPaint)
                }
            }
        }
    }
}
