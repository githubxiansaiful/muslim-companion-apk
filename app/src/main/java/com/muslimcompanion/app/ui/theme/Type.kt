package com.muslimcompanion.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.muslimcompanion.app.R

val PurnoPranFontFamily = FontFamily(
    Font(R.font.purno_pran, FontWeight.Normal),
    Font(R.font.purno_pran, FontWeight.Medium),
    Font(R.font.purno_pran, FontWeight.SemiBold),
    Font(R.font.purno_pran, FontWeight.Bold),
    Font(R.font.purno_pran, FontWeight.ExtraBold),
    Font(R.font.purno_pran_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.purno_pran_italic, FontWeight.Bold, FontStyle.Italic)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 42.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp
    ),
    displaySmall = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PurnoPranFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)
