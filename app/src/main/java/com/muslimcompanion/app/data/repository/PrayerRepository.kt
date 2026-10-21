package com.muslimcompanion.app.data.repository

import com.batoulapps.adhan.CalculationMethod as AdhanMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab as AdhanMadhab
import com.batoulapps.adhan.Prayer
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.Qibla
import com.batoulapps.adhan.data.DateComponents
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.CalculationMethod
import com.muslimcompanion.app.data.model.DayPrayerSchedule
import com.muslimcompanion.app.data.model.ForbiddenTimeItem
import com.muslimcompanion.app.data.model.HijriDateInfo
import com.muslimcompanion.app.data.model.Madhab
import com.muslimcompanion.app.data.model.NaflPrayerItem
import com.muslimcompanion.app.data.model.PrayerTimeItem
import com.muslimcompanion.app.data.model.QiblaInfo
import java.text.SimpleDateFormat
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object PrayerRepository {

    private val scheduleCache = java.util.concurrent.ConcurrentHashMap<String, DayPrayerSchedule>()
    private val qiblaCache = java.util.concurrent.ConcurrentHashMap<String, QiblaInfo>()

    private fun getCalculationParameters(method: CalculationMethod, madhab: Madhab): CalculationParameters {
        val params = when (method) {
            CalculationMethod.MUSLIM_WORLD_LEAGUE -> AdhanMethod.MUSLIM_WORLD_LEAGUE.parameters
            CalculationMethod.UMM_AL_QURA -> AdhanMethod.UMM_AL_QURA.parameters
            CalculationMethod.EGYPTIAN -> AdhanMethod.EGYPTIAN.parameters
            CalculationMethod.NORTH_AMERICA -> AdhanMethod.NORTH_AMERICA.parameters
            CalculationMethod.DUBAI -> AdhanMethod.DUBAI.parameters
            CalculationMethod.KARACHI -> AdhanMethod.KARACHI.parameters
        }

        params.madhab = if (madhab == Madhab.HANAFI) {
            AdhanMadhab.HANAFI
        } else {
            AdhanMadhab.SHAFI
        }

        return params
    }

    fun calculateSchedule(
        latitude: Double,
        longitude: Double,
        cityName: String,
        countryName: String,
        method: CalculationMethod,
        madhab: Madhab,
        date: Date = Date()
    ): DayPrayerSchedule {
        val cal = Calendar.getInstance().apply { time = date }
        val minuteBucket = System.currentTimeMillis() / 60000L
        val cacheKey = "$latitude,$longitude,$cityName,$countryName,${method.name},${madhab.name},${cal.get(Calendar.DAY_OF_YEAR)},$minuteBucket"
        scheduleCache[cacheKey]?.let { return it }

        val coordinates = Coordinates(latitude, longitude)
        val dateComponents = DateComponents(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )

        val params = getCalculationParameters(method, madhab)
        val prayerTimes = PrayerTimes(coordinates, dateComponents, params)

        val now = Date()
        val currentPrayerCode = prayerTimes.currentPrayer(now)
        val nextPrayerCode = prayerTimes.nextPrayer(now)

        val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)

        fun formatTime12(d: Date?): String {
            if (d == null) return "--:--"
            val enTime = timeFormat.format(d)
            val parts = enTime.split(" ")
            val digits = PrayerConstants.toBengaliDigits(parts[0])
            val marker = if (parts.size > 1 && parts[1].equals("PM", ignoreCase = true)) "বিকাল/সন্ধ্যা" else "ভোর/সকাল"
            return "$digits ${if (parts.size > 1 && parts[1].equals("AM", ignoreCase = true)) "AM" else "PM"}"
        }

        val simpleTimeFormat = SimpleDateFormat("h:mm", Locale.ENGLISH)
        val twoDigitFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)

        fun formatSimpleTime(d: Date?): String {
            if (d == null) return "--:--"
            return PrayerConstants.toBengaliDigits(simpleTimeFormat.format(d))
        }

        fun formatTwoDigit(d: Date?): String {
            if (d == null) return "--:--"
            return PrayerConstants.toBengaliDigits(twoDigitFormat.format(d))
        }

        // Extended timings
        val sahurEndDate = Date(prayerTimes.fajr.time - 5 * 60 * 1000)
        val fajrEndDate = Date(prayerTimes.sunrise.time - 1 * 60 * 1000)
        val ishraqStartDate = Date(prayerTimes.sunrise.time + 15 * 60 * 1000)
        val ishraqEndDate = Date(prayerTimes.dhuhr.time - 15 * 60 * 1000)
        val chashtStartDate = Date(prayerTimes.sunrise.time + 105 * 60 * 1000)
        val zawalStartDate = Date(prayerTimes.dhuhr.time - 6 * 60 * 1000)
        val dhuhrEndDate = Date(prayerTimes.asr.time - 1 * 60 * 1000)
        val sunsetDate = Date(prayerTimes.maghrib.time - 3 * 60 * 1000)
        val asrEndDate = sunsetDate
        val maghribEndDate = Date(prayerTimes.isha.time - 1 * 60 * 1000)

        // Midnight (Nisf al-Layl)
        val midnightMillis = prayerTimes.maghrib.time + ((prayerTimes.fajr.time + 24 * 3600 * 1000 - prayerTimes.maghrib.time) / 2)
        val midnightDate = Date(midnightMillis)

        val fajrItem = PrayerTimeItem(
            id = "fajr",
            nameEn = "Fajr",
            nameBn = "ফজর",
            nameAr = "الفجر",
            timeFormatted = formatTime12(prayerTimes.fajr),
            date = prayerTimes.fajr,
            endDate = fajrEndDate,
            endTimeFormatted = formatSimpleTime(fajrEndDate),
            rangeFormatted = "${formatSimpleTime(prayerTimes.fajr)} - ${formatSimpleTime(fajrEndDate)}",
            isCurrent = currentPrayerCode == Prayer.FAJR,
            isNext = nextPrayerCode == Prayer.FAJR,
            isPassed = now.after(prayerTimes.fajr)
        )

        val sunriseItem = PrayerTimeItem(
            id = "sunrise",
            nameEn = "Sunrise",
            nameBn = "সূর্যোদয়",
            nameAr = "الشروق",
            timeFormatted = formatTime12(prayerTimes.sunrise),
            date = prayerTimes.sunrise,
            endDate = ishraqStartDate,
            endTimeFormatted = formatSimpleTime(ishraqStartDate),
            rangeFormatted = "${formatSimpleTime(prayerTimes.sunrise)} - ${formatSimpleTime(ishraqStartDate)}",
            isCurrent = currentPrayerCode == Prayer.SUNRISE,
            isNext = nextPrayerCode == Prayer.SUNRISE,
            isPassed = now.after(prayerTimes.sunrise)
        )

        val dhuhrItem = PrayerTimeItem(
            id = "dhuhr",
            nameEn = "Dhuhr",
            nameBn = "যোহর",
            nameAr = "الظهر",
            timeFormatted = formatTime12(prayerTimes.dhuhr),
            date = prayerTimes.dhuhr,
            endDate = dhuhrEndDate,
            endTimeFormatted = formatSimpleTime(dhuhrEndDate),
            rangeFormatted = "${formatSimpleTime(prayerTimes.dhuhr)} - ${formatSimpleTime(dhuhrEndDate)}",
            isCurrent = currentPrayerCode == Prayer.DHUHR,
            isNext = nextPrayerCode == Prayer.DHUHR,
            isPassed = now.after(prayerTimes.dhuhr)
        )

        val asrItem = PrayerTimeItem(
            id = "asr",
            nameEn = "Asr",
            nameBn = "আসর",
            nameAr = "العصر",
            timeFormatted = formatTime12(prayerTimes.asr),
            date = prayerTimes.asr,
            endDate = asrEndDate,
            endTimeFormatted = formatSimpleTime(asrEndDate),
            rangeFormatted = "${formatSimpleTime(prayerTimes.asr)} - ${formatSimpleTime(asrEndDate)}",
            isCurrent = currentPrayerCode == Prayer.ASR,
            isNext = nextPrayerCode == Prayer.ASR,
            isPassed = now.after(prayerTimes.asr)
        )

        val maghribItem = PrayerTimeItem(
            id = "maghrib",
            nameEn = "Maghrib",
            nameBn = "মাগরিব",
            nameAr = "المغرب",
            timeFormatted = formatTime12(prayerTimes.maghrib),
            date = prayerTimes.maghrib,
            endDate = maghribEndDate,
            endTimeFormatted = formatSimpleTime(maghribEndDate),
            rangeFormatted = "${formatSimpleTime(prayerTimes.maghrib)} - ${formatSimpleTime(maghribEndDate)}",
            isCurrent = currentPrayerCode == Prayer.MAGHRIB,
            isNext = nextPrayerCode == Prayer.MAGHRIB,
            isPassed = now.after(prayerTimes.maghrib)
        )

        val ishaItem = PrayerTimeItem(
            id = "isha",
            nameEn = "Isha",
            nameBn = "ইশা",
            nameAr = "العشاء",
            timeFormatted = formatTime12(prayerTimes.isha),
            date = prayerTimes.isha,
            endDate = sahurEndDate,
            endTimeFormatted = formatSimpleTime(sahurEndDate),
            rangeFormatted = "${formatSimpleTime(prayerTimes.isha)} - ${formatSimpleTime(sahurEndDate)}",
            isCurrent = currentPrayerCode == Prayer.ISHA,
            isNext = nextPrayerCode == Prayer.ISHA,
            isPassed = now.after(prayerTimes.isha)
        )

        val allList = listOf(fajrItem, sunriseItem, dhuhrItem, asrItem, maghribItem, ishaItem)

        // Celestial sky progress & Day/Night determination
        val isNight = now.after(sunsetDate) || now.before(prayerTimes.sunrise)
        val celestialPct = if (isNight) {
            val nightStart = if (now.before(prayerTimes.sunrise)) sunsetDate.time - 24 * 3600 * 1000L else sunsetDate.time
            val nightEnd = if (now.before(prayerTimes.sunrise)) prayerTimes.sunrise.time else prayerTimes.sunrise.time + 24 * 3600 * 1000L
            val nightDuration = (nightEnd - nightStart).coerceAtLeast(1L)
            ((now.time - nightStart).toFloat() / nightDuration.toFloat()).coerceIn(0.06f, 0.94f)
        } else {
            val dayStart = prayerTimes.sunrise.time
            val dayEnd = sunsetDate.time
            val dayDuration = (dayEnd - dayStart).coerceAtLeast(1L)
            ((now.time - dayStart).toFloat() / dayDuration.toFloat()).coerceIn(0.06f, 0.94f)
        }

        // Active Prayer determination matching Muslim Bangla Hero Card
        var leftTitle = ""
        var leftTimeStr = ""
        var rightTitle = ""
        var rightTimeStr = ""
        var rightHasCheck = false
        var activeRangeStr = ""

        val (activeName, activeTimeStr, activeStatus, progressPct, remainingTargetDate) = when {
            now.after(prayerTimes.isha) || now.before(sahurEndDate) -> {
                // Late night / Isha - Tahajjud
                val start = prayerTimes.isha.time
                val end = sahurEndDate.time + if (now.after(prayerTimes.isha)) 24 * 3600 * 1000 else 0L
                val currentT = now.time + if (now.before(sahurEndDate) && prayerTimes.isha.after(now)) 0L else 0L
                val pct = if (end > start) ((currentT - start).toFloat() / (end - start)).coerceIn(0.05f, 0.95f) else 0.5f
                val target = if (now.before(sahurEndDate)) sahurEndDate else Date(sahurEndDate.time + 24 * 3600 * 1000)
                
                leftTitle = "ইশা"
                leftTimeStr = "${formatSimpleTime(prayerTimes.isha)} মি."
                rightTitle = "তাহাজ্জুদ শেষ"
                rightTimeStr = "${formatSimpleTime(sahurEndDate)} মি."
                rightHasCheck = true
                activeRangeStr = "${formatTwoDigit(prayerTimes.isha)} - ${formatTwoDigit(sahurEndDate)}"

                Tuple5("ইশা", formatSimpleTime(prayerTimes.isha), "চলমান", pct, target)
            }
            now.before(prayerTimes.fajr) -> {
                leftTitle = "সাহরি শেষ"
                leftTimeStr = "${formatSimpleTime(sahurEndDate)} মি."
                rightTitle = "ফজর শুরু"
                rightTimeStr = "${formatSimpleTime(prayerTimes.fajr)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(prayerTimes.fajr)} - ${formatTwoDigit(prayerTimes.sunrise)}"

                Tuple5("ফজর", formatSimpleTime(prayerTimes.fajr), "আসন্ন", 0.95f, prayerTimes.fajr)
            }
            now.before(prayerTimes.sunrise) -> {
                val pct = ((now.time - prayerTimes.fajr.time).toFloat() / (prayerTimes.sunrise.time - prayerTimes.fajr.time)).coerceIn(0.05f, 0.95f)
                leftTitle = "ফজর"
                leftTimeStr = "${formatSimpleTime(prayerTimes.fajr)} মি."
                rightTitle = "সূর্যোদয়"
                rightTimeStr = "${formatSimpleTime(prayerTimes.sunrise)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(prayerTimes.fajr)} - ${formatTwoDigit(prayerTimes.sunrise)}"

                Tuple5("ফজর", formatSimpleTime(prayerTimes.fajr), "চলমান", pct, prayerTimes.sunrise)
            }
            now.before(ishraqStartDate) -> {
                leftTitle = "সূর্যোদয়"
                leftTimeStr = "${formatSimpleTime(prayerTimes.sunrise)} মি."
                rightTitle = "ইশরাক"
                rightTimeStr = "${formatSimpleTime(ishraqStartDate)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(prayerTimes.sunrise)} - ${formatTwoDigit(ishraqStartDate)}"

                Tuple5("সূর্যোদয়", formatSimpleTime(prayerTimes.sunrise), "চলমান", 0.5f, ishraqStartDate)
            }
            now.before(prayerTimes.dhuhr) -> {
                val name = if (now.before(chashtStartDate)) "ইশরাক" else "চাশত"
                val startTime = if (now.before(chashtStartDate)) ishraqStartDate else chashtStartDate
                val pct = ((now.time - ishraqStartDate.time).toFloat() / (prayerTimes.dhuhr.time - ishraqStartDate.time)).coerceIn(0.05f, 0.95f)
                leftTitle = name
                leftTimeStr = "${formatSimpleTime(startTime)} মি."
                rightTitle = "যোহর"
                rightTimeStr = "${formatSimpleTime(prayerTimes.dhuhr)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(startTime)} - ${formatTwoDigit(prayerTimes.dhuhr)}"

                Tuple5(name, formatSimpleTime(startTime), "চলমান", pct, prayerTimes.dhuhr)
            }
            now.before(prayerTimes.asr) -> {
                val pct = ((now.time - prayerTimes.dhuhr.time).toFloat() / (prayerTimes.asr.time - prayerTimes.dhuhr.time)).coerceIn(0.05f, 0.95f)
                leftTitle = "যোহর"
                leftTimeStr = "${formatSimpleTime(prayerTimes.dhuhr)} মি."
                rightTitle = "আসর"
                rightTimeStr = "${formatSimpleTime(prayerTimes.asr)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(prayerTimes.dhuhr)} - ${formatTwoDigit(prayerTimes.asr)}"

                Tuple5("যোহর", formatSimpleTime(prayerTimes.dhuhr), "চলমান", pct, prayerTimes.asr)
            }
            now.before(prayerTimes.maghrib) -> {
                val pct = ((now.time - prayerTimes.asr.time).toFloat() / (prayerTimes.maghrib.time - prayerTimes.asr.time)).coerceIn(0.05f, 0.95f)
                leftTitle = "আসর"
                leftTimeStr = "${formatSimpleTime(prayerTimes.asr)} মি."
                rightTitle = "মাগরিব"
                rightTimeStr = "${formatSimpleTime(prayerTimes.maghrib)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(prayerTimes.asr)} - ${formatTwoDigit(prayerTimes.maghrib)}"

                Tuple5("আসর", formatSimpleTime(prayerTimes.asr), "চলমান", pct, prayerTimes.maghrib)
            }
            else -> {
                // Maghrib to Isha
                val pct = ((now.time - prayerTimes.maghrib.time).toFloat() / (prayerTimes.isha.time - prayerTimes.maghrib.time)).coerceIn(0.05f, 0.95f)
                leftTitle = "মাগরিব"
                leftTimeStr = "${formatSimpleTime(prayerTimes.maghrib)} মি."
                rightTitle = "ইশা"
                rightTimeStr = "${formatSimpleTime(prayerTimes.isha)} মি."
                rightHasCheck = false
                activeRangeStr = "${formatTwoDigit(prayerTimes.maghrib)} - ${formatTwoDigit(prayerTimes.isha)}"

                Tuple5("মাগরিব", formatSimpleTime(prayerTimes.maghrib), "চলমান", pct, prayerTimes.isha)
            }
        }

        val remainingMillis = if (remainingTargetDate.after(now)) {
            remainingTargetDate.time - now.time
        } else {
            0L
        }

        val remainingMinutes = remainingMillis / (1000 * 60)
        val hours = remainingMinutes / 60
        val mins = remainingMinutes % 60
        val formattedRemaining = if (hours > 0) {
            "${PrayerConstants.toBengaliDigits(hours)} ঘণ্টা ${PrayerConstants.toBengaliDigits(mins)} মিনিট বাকি"
        } else {
            "${PrayerConstants.toBengaliDigits(mins)} মিনিট বাকি"
        }

        val currentName = when (currentPrayerCode) {
            Prayer.FAJR -> "ফজর"
            Prayer.SUNRISE -> "ইশরাক"
            Prayer.DHUHR -> "যোহর"
            Prayer.ASR -> "আসর"
            Prayer.MAGHRIB -> "মাগরিব"
            Prayer.ISHA -> "ইশা"
            else -> null
        }

        val nextName = when (nextPrayerCode) {
            Prayer.FAJR -> "ফজর"
            Prayer.SUNRISE -> "সূর্যোদয়"
            Prayer.DHUHR -> "যোহর"
            Prayer.ASR -> "আসর"
            Prayer.MAGHRIB -> "মাগরিব"
            Prayer.ISHA -> "ইশা"
            else -> "ফজর (আগামীকাল)"
        }

        val naflList = listOf(
            NaflPrayerItem("tahajjud", "তাহাজ্জুদ", "tahajjud", "--:--", formatSimpleTime(sahurEndDate)),
            NaflPrayerItem("ishraq", "ইশরাক", "ishraq", formatSimpleTime(ishraqStartDate), formatSimpleTime(ishraqEndDate)),
            NaflPrayerItem("chasht", "চাশত", "chasht", formatSimpleTime(chashtStartDate), formatSimpleTime(ishraqEndDate)),
            NaflPrayerItem("zawal", "যাওয়াাল", "zawal", formatSimpleTime(prayerTimes.dhuhr), "--:--")
        )

        val forbiddenList = listOf(
            ForbiddenTimeItem("sunrise", "সূর্যোদয়", "sunrise", "${formatSimpleTime(prayerTimes.sunrise)} - ${formatSimpleTime(ishraqStartDate)}"),
            ForbiddenTimeItem("noon", "দুপুর", "noon", "${formatSimpleTime(zawalStartDate)} - ${formatSimpleTime(prayerTimes.dhuhr)}"),
            ForbiddenTimeItem("sunset", "সূর্যাস্ত", "sunset", "${formatSimpleTime(Date(prayerTimes.maghrib.time - 15 * 60 * 1000))} - ${formatSimpleTime(prayerTimes.maghrib)}")
        )

        val result = DayPrayerSchedule(
            date = date,
            city = cityName,
            country = countryName,
            fajr = fajrItem,
            sunrise = sunriseItem,
            dhuhr = dhuhrItem,
            asr = asrItem,
            maghrib = maghribItem,
            isha = ishaItem,
            currentPrayer = currentName,
            nextPrayer = nextName,
            timeRemainingNextMinutes = remainingMinutes,
            formattedRemainingNext = formattedRemaining,
            allPrayers = allList,
            sahurEndTimeFormatted = formatSimpleTime(sahurEndDate),
            iftarTimeFormatted = formatSimpleTime(prayerTimes.maghrib),
            ishraqTimeFormatted = formatSimpleTime(ishraqStartDate),
            midnightTimeFormatted = formatSimpleTime(midnightDate),
            sunsetTimeFormatted = formatSimpleTime(sunsetDate),
            sunriseTimeFormatted = formatSimpleTime(prayerTimes.sunrise),
            activePrayerName = activeName,
            activePrayerTime = activeTimeStr,
            activePrayerRange = activeRangeStr,
            activePrayerStatus = activeStatus,
            progressFraction = progressPct,
            leftPillarTitle = leftTitle,
            leftPillarTime = leftTimeStr,
            rightPillarTitle = rightTitle,
            rightPillarTime = rightTimeStr,
            rightPillarHasCheck = rightHasCheck,
            celestialProgress = celestialPct,
            isNight = isNight,
            naflPrayers = naflList,
            forbiddenTimes = forbiddenList
        )

        if (scheduleCache.size > 20) {
            scheduleCache.clear()
        }
        scheduleCache[cacheKey] = result
        return result
    }

    private data class Tuple5<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

    fun calculateQibla(latitude: Double, longitude: Double): QiblaInfo {
        val qiblaKey = "$latitude,$longitude"
        qiblaCache[qiblaKey]?.let { return it }

        val coordinates = Coordinates(latitude, longitude)
        val qibla = Qibla(coordinates)
        val angle = qibla.direction

        // Distance to Makkah (21.4225, 39.8262) via Haversine formula
        val makkahLat = 21.4225
        val makkahLng = 39.8262

        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(makkahLat - latitude)
        val dLng = Math.toRadians(makkahLng - longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(makkahLat)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distanceKm = r * c

        val result = QiblaInfo(
            directionDegrees = Math.round(angle * 10.0) / 10.0,
            compassAngle = angle,
            distanceKm = Math.round(distanceKm).toDouble()
        )
        qiblaCache[qiblaKey] = result
        return result
    }

    fun getHijriDate(): HijriDateInfo {
        return try {
            val hDate = HijrahDate.now()
            val day = hDate.get(ChronoField.DAY_OF_MONTH)
            val month = hDate.get(ChronoField.MONTH_OF_YEAR)
            val year = hDate.get(ChronoField.YEAR)

            val monthsEn = listOf(
                "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                "Ramadan", "Shawwal", "Dhul-Qi'dah", "Dhul-Hijjah"
            )

            val monthsBn = listOf(
                "মুহররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
                "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শা'বান",
                "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্জ"
            )

            val idx = (month - 1).coerceIn(0, 11)
            val monthNameEn = monthsEn[idx]
            val monthNameBn = monthsBn[idx]

            HijriDateInfo(
                day = day,
                monthNameEn = monthNameEn,
                monthNameBn = monthNameBn,
                year = year,
                formattedEn = "$day $monthNameEn $year AH",
                formattedBn = "${PrayerConstants.toBengaliDigits(day)} $monthNameBn ${PrayerConstants.toBengaliDigits(year)} হিজরি"
            )
        } catch (e: Exception) {
            HijriDateInfo(1, "Ramadan", "রমজান", 1448, "1 Ramadan 1448 AH", "১ রমজান ১৪৪৮ হিজরি")
        }
    }
}
