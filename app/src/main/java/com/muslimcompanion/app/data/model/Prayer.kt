package com.muslimcompanion.app.data.model

import java.util.Date

data class CityOption(
    val name: String,
    val nameBn: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)

enum class CalculationMethod(val idString: String, val titleBn: String) {
    KARACHI("Karachi", "ইসলামিক বিজ্ঞান বিশ্ববিদ্যালয়, করাচি (বাংলাদেশ ও দক্ষিণ এশিয়া)"),
    MUSLIM_WORLD_LEAGUE("MuslimWorldLeague", "মুসলিম ওয়ার্ল্ড লীগ (ইউরোপ ও পূর্ব এশিয়া)"),
    UMM_AL_QURA("UmmAlQura", "উম্মুল কুরা বিশ্ববিদ্যালয়, মক্কা মুকাররমা"),
    EGYPTIAN("Egyptian", "মিশরীয় জেনারেল অথরিটি অব সার্ভে"),
    NORTH_AMERICA("NorthAmerica", "ইসলামিক সোসাইটি অব নর্থ আমেরিকা"),
    DUBAI("Dubai", "দুবাই ইসলামিক অ্যাফেয়ার্স")
}

enum class Madhab(val idString: String, val titleBn: String) {
    HANAFI("Hanafi", "হানাফি (আসর কিছুটা বিলম্বে শুরু)"),
    SHAFI("Shafi", "শাফেয়ি / হাম্বলি / মালিকি (আসর প্রথম ওয়াক্তে শুরু)")
}

data class PrayerTimeItem(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val nameAr: String,
    val timeFormatted: String,
    val date: Date,
    val endDate: Date? = null,
    val endTimeFormatted: String = "",
    val rangeFormatted: String = "",
    val isCurrent: Boolean = false,
    val isNext: Boolean = false,
    val isPassed: Boolean = false
)

data class NaflPrayerItem(
    val id: String,
    val nameBn: String,
    val iconType: String,
    val startTimeFormatted: String,
    val endTimeFormatted: String,
    val hasAlert: Boolean = true
)

data class ForbiddenTimeItem(
    val id: String,
    val nameBn: String,
    val iconType: String,
    val rangeFormatted: String,
    val hasAlert: Boolean = true
)

data class DayPrayerSchedule(
    val date: Date,
    val city: String,
    val country: String,
    val fajr: PrayerTimeItem,
    val sunrise: PrayerTimeItem,
    val dhuhr: PrayerTimeItem,
    val asr: PrayerTimeItem,
    val maghrib: PrayerTimeItem,
    val isha: PrayerTimeItem,
    val currentPrayer: String?,
    val nextPrayer: String?,
    val timeRemainingNextMinutes: Long,
    val formattedRemainingNext: String,
    val allPrayers: List<PrayerTimeItem>,
    val sahurEndTimeFormatted: String = "",
    val iftarTimeFormatted: String = "",
    val ishraqTimeFormatted: String = "",
    val midnightTimeFormatted: String = "",
    val sunsetTimeFormatted: String = "",
    val sunriseTimeFormatted: String = "",
    val activePrayerName: String = "",
    val activePrayerTime: String = "",
    val activePrayerRange: String = "",
    val activePrayerStatus: String = "চলমান",
    val progressFraction: Float = 0.5f,
    val leftPillarTitle: String = "",
    val leftPillarTime: String = "",
    val rightPillarTitle: String = "",
    val rightPillarTime: String = "",
    val rightPillarHasCheck: Boolean = false,
    val celestialProgress: Float = 0.5f,
    val isNight: Boolean = false,
    val naflPrayers: List<NaflPrayerItem> = emptyList(),
    val forbiddenTimes: List<ForbiddenTimeItem> = emptyList()
)

data class QiblaInfo(
    val directionDegrees: Double,
    val compassAngle: Double,
    val distanceKm: Double
)

data class HijriDateInfo(
    val day: Int,
    val monthNameEn: String,
    val monthNameBn: String,
    val year: Int,
    val formattedEn: String,
    val formattedBn: String
)
