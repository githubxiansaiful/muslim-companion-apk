package com.muslimcompanion.app.data.model

data class SurahMeta(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val bengaliName: String,
    val numberOfAyahs: Int,
    val revelationType: String
)

data class AyahItem(
    val number: Int,
    val numberInSurah: Int,
    val text: String,
    val pronunciationBn: String,
    val translationBn: String,
    val audioUrl: String
)

data class QuranReaderSettings(
    val arabicFontSize: Int = 28,
    val bengaliFontSize: Int = 15,
    val viewMode: String = "mushaf", // "mushaf" or "card"
    val stripMode: String = "double", // "double" or "single"
    val showArabic: Boolean = true,
    val showPronunciation: Boolean = true,
    val showTranslation: Boolean = true,
    val theme: String = "paper" // "paper", "sepia", "white", "dark"
)

data class QuranProgress(
    val lastSurah: Int = 1,
    val lastAyah: Int = 1,
    val totalAyahsRead: Int = 0,
    val streakDays: Int = 1,
    val dailyTarget: Int = 10,
    val todayReadCount: Int = 0
)
