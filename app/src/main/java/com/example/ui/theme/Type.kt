package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins, FontWeight.Normal),
    Font(R.font.poppins, FontWeight.Medium),
    Font(R.font.poppins, FontWeight.SemiBold),
    Font(R.font.poppins, FontWeight.Bold),
    Font(R.font.poppins, FontWeight.ExtraBold)
)

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto, FontWeight.Normal),
    Font(R.font.roboto, FontWeight.Medium),
    Font(R.font.roboto, FontWeight.Bold)
)

// Typography hierarchy matching Phase 2 specification:
// - Main page title: 22–24sp
// - Main financial amount: 38–42sp
// - Section headings: 19–21sp
// - Card titles: 16–18sp
// - Descriptions: 13–15sp
// - Bottom navigation labels: 12–13sp
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        color = PrimaryNavy
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 23.sp,
        lineHeight = 30.sp,
        color = CardWhite
    ),
    titleLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = PrimaryText
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        color = PrimaryText
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        color = SecondaryText
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.5.sp,
        lineHeight = 16.sp,
        color = SecondaryText
    )
)
