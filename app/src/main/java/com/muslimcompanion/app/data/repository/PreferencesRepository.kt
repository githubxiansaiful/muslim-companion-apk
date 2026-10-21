package com.muslimcompanion.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muslimcompanion.app.data.constants.PrayerConstants
import com.muslimcompanion.app.data.model.CalculationMethod
import com.muslimcompanion.app.data.model.CityOption
import com.muslimcompanion.app.data.model.DailyAmalItem
import com.muslimcompanion.app.data.model.Madhab
import com.muslimcompanion.app.data.model.QuranProgress
import com.muslimcompanion.app.data.model.QuranReaderSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("mc_user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    // 0. User Name
    private val _userName = MutableStateFlow(prefs.getString("user_name", "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name.trim()).apply()
        _userName.value = name.trim()
    }

    // 1. City / Location
    private val _currentCity = MutableStateFlow(loadCity())
    val currentCity: StateFlow<CityOption> = _currentCity.asStateFlow()

    private fun loadCity(): CityOption {
        val name = prefs.getString("city_name", "ঢাকা") ?: "ঢাকা"
        return PrayerConstants.POPULAR_CITIES.find { it.name == name } ?: CityOption(
            name = name,
            nameBn = prefs.getString("city_name_bn", name) ?: name,
            country = prefs.getString("city_country", "বাংলাদেশ") ?: "বাংলাদেশ",
            latitude = prefs.getFloat("city_lat", 23.8103f).toDouble(),
            longitude = prefs.getFloat("city_lng", 90.4125f).toDouble(),
            timezone = prefs.getString("city_timezone", "Asia/Dhaka") ?: "Asia/Dhaka"
        )
    }

    fun saveCity(city: CityOption) {
        prefs.edit()
            .putString("city_name", city.name)
            .putString("city_name_bn", city.nameBn)
            .putString("city_country", city.country)
            .putFloat("city_lat", city.latitude.toFloat())
            .putFloat("city_lng", city.longitude.toFloat())
            .putString("city_timezone", city.timezone)
            .apply()
        _currentCity.value = city
    }

    // 2. Calculation Method & Madhab
    private val _calculationMethod = MutableStateFlow(loadCalculationMethod())
    val calculationMethod: StateFlow<CalculationMethod> = _calculationMethod.asStateFlow()

    private fun loadCalculationMethod(): CalculationMethod {
        val str = prefs.getString("calc_method", "KARACHI") ?: "KARACHI"
        return try { CalculationMethod.valueOf(str) } catch (e: Exception) { CalculationMethod.KARACHI }
    }

    fun saveCalculationMethod(method: CalculationMethod) {
        prefs.edit().putString("calc_method", method.name).apply()
        _calculationMethod.value = method
    }

    private val _madhab = MutableStateFlow(loadMadhab())
    val madhab: StateFlow<Madhab> = _madhab.asStateFlow()

    private fun loadMadhab(): Madhab {
        val str = prefs.getString("madhab", "HANAFI") ?: "HANAFI"
        return try { Madhab.valueOf(str) } catch (e: Exception) { Madhab.HANAFI }
    }

    fun saveMadhab(madhab: Madhab) {
        prefs.edit().putString("madhab", madhab.name).apply()
        _madhab.value = madhab
    }

    // 3. Quran Reader Settings
    private val _readerSettings = MutableStateFlow(loadReaderSettings())
    val readerSettings: StateFlow<QuranReaderSettings> = _readerSettings.asStateFlow()

    private fun loadReaderSettings(): QuranReaderSettings {
        return QuranReaderSettings(
            arabicFontSize = prefs.getInt("quran_arabic_size", 28),
            bengaliFontSize = prefs.getInt("quran_bengali_size", 17),
            viewMode = prefs.getString("quran_view_mode", "mushaf") ?: "mushaf",
            stripMode = prefs.getString("quran_strip_mode", "double") ?: "double",
            showArabic = prefs.getBoolean("quran_show_arabic", true),
            showPronunciation = prefs.getBoolean("quran_show_pronunciation", true),
            showTranslation = prefs.getBoolean("quran_show_translation", true),
            theme = prefs.getString("quran_theme", "paper") ?: "paper"
        )
    }

    fun updateReaderSettings(settings: QuranReaderSettings) {
        prefs.edit()
            .putInt("quran_arabic_size", settings.arabicFontSize)
            .putInt("quran_bengali_size", settings.bengaliFontSize)
            .putString("quran_view_mode", settings.viewMode)
            .putString("quran_strip_mode", settings.stripMode)
            .putBoolean("quran_show_arabic", settings.showArabic)
            .putBoolean("quran_show_pronunciation", settings.showPronunciation)
            .putBoolean("quran_show_translation", settings.showTranslation)
            .putString("quran_theme", settings.theme)
            .apply()
        _readerSettings.value = settings
    }

    // 4. Quran Progress
    private val _quranProgress = MutableStateFlow(loadQuranProgress())
    val quranProgress: StateFlow<QuranProgress> = _quranProgress.asStateFlow()

    private fun loadQuranProgress(): QuranProgress {
        return QuranProgress(
            lastSurah = prefs.getInt("quran_last_surah", 1),
            lastAyah = prefs.getInt("quran_last_ayah", 1),
            totalAyahsRead = prefs.getInt("quran_total_read", 7),
            streakDays = prefs.getInt("quran_streak", 3),
            dailyTarget = prefs.getInt("quran_target", 10),
            todayReadCount = prefs.getInt("quran_today_read", 5)
        )
    }

    fun saveLastRead(surah: Int, ayah: Int) {
        val current = _quranProgress.value
        val updated = current.copy(
            lastSurah = surah,
            lastAyah = ayah,
            totalAyahsRead = current.totalAyahsRead + 1,
            todayReadCount = (current.todayReadCount + 1).coerceAtMost(current.dailyTarget)
        )
        prefs.edit()
            .putInt("quran_last_surah", surah)
            .putInt("quran_last_ayah", ayah)
            .putInt("quran_total_read", updated.totalAyahsRead)
            .putInt("quran_today_read", updated.todayReadCount)
            .apply()
        _quranProgress.value = updated
    }

    // 5. Quran Ayah Bookmarks
    private val _bookmarkedAyahs = MutableStateFlow(loadBookmarkedAyahs())
    val bookmarkedAyahs: StateFlow<Set<String>> = _bookmarkedAyahs.asStateFlow()

    private fun loadBookmarkedAyahs(): Set<String> {
        return prefs.getStringSet("bookmarked_ayahs", emptySet()) ?: emptySet()
    }

    fun toggleAyahBookmark(surah: Int, ayah: Int) {
        val key = "$surah:$ayah"
        val current = _bookmarkedAyahs.value.toMutableSet()
        if (current.contains(key)) {
            current.remove(key)
        } else {
            current.add(key)
        }
        prefs.edit().putStringSet("bookmarked_ayahs", current).apply()
        _bookmarkedAyahs.value = current
    }

    // 6. Dua Bookmarks
    private val _bookmarkedDuas = MutableStateFlow(loadBookmarkedDuas())
    val bookmarkedDuas: StateFlow<Set<String>> = _bookmarkedDuas.asStateFlow()

    private fun loadBookmarkedDuas(): Set<String> {
        return prefs.getStringSet("bookmarked_duas", emptySet()) ?: emptySet()
    }

    fun toggleDuaBookmark(duaId: String) {
        val current = _bookmarkedDuas.value.toMutableSet()
        if (current.contains(duaId)) {
            current.remove(duaId)
        } else {
            current.add(duaId)
        }
        prefs.edit().putStringSet("bookmarked_duas", current).apply()
        _bookmarkedDuas.value = current
    }

    // 7. Tasbih Today Counter
    private val _tasbihToday = MutableStateFlow(loadTasbihToday())
    val tasbihToday: StateFlow<Int> = _tasbihToday.asStateFlow()

    private fun loadTasbihToday(): Int {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val savedDate = prefs.getString("tasbih_date", "")
        if (savedDate != todayStr) {
            prefs.edit().putString("tasbih_date", todayStr).putInt("tasbih_count", 0).apply()
            return 0
        }
        return prefs.getInt("tasbih_count", 0)
    }

    fun incrementTasbih() {
        val newCount = _tasbihToday.value + 1
        prefs.edit().putInt("tasbih_count", newCount).apply()
        _tasbihToday.value = newCount
    }

    // 8. Daily Amal Checklist
    private val defaultAmalList = listOf(
        DailyAmalItem("1", "ফজর নামাজ ওয়াক্তে আদায়", "prayer"),
        DailyAmalItem("2", "যোহর নামাজ ওয়াক্তে আদায়", "prayer"),
        DailyAmalItem("3", "আসর নামাজ ওয়াক্তে আদায়", "prayer"),
        DailyAmalItem("4", "মাগরিব নামাজ ওয়াক্তে আদায়", "prayer"),
        DailyAmalItem("5", "ইশা নামাজ ওয়াক্তে আদায়", "prayer"),
        DailyAmalItem("6", "দৈনিক কুরআনুল কারীম তিলাওয়াত", "quran"),
        DailyAmalItem("7", "সকালের মাসনুন যিকির পাঠ", "zikr"),
        DailyAmalItem("8", "সন্ধ্যার মাসনুন যিকির পাঠ", "zikr"),
        DailyAmalItem("9", "প্রিয় নবী ﷺ এর প্রতি দরূদ শরীফ (১০০ বার)", "sunnah"),
        DailyAmalItem("10", "তওবা ও ইস্তিগফার পাঠ (১০০ বার)", "sunnah"),
        DailyAmalItem("11", "সাদাকাহ ও মানুষের সাথে সদ্ব্যবহার", "charity")
    )

    private val _dailyAmal = MutableStateFlow(loadDailyAmal())
    val dailyAmal: StateFlow<List<DailyAmalItem>> = _dailyAmal.asStateFlow()

    private fun loadDailyAmal(): List<DailyAmalItem> {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val json = prefs.getString("amal_$todayStr", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<DailyAmalItem>>() {}.type
                return gson.fromJson(json, type)
            } catch (e: Exception) {
                // fallback
            }
        }
        return defaultAmalList
    }

    fun toggleAmalItem(id: String) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val current = _dailyAmal.value.map { item ->
            if (item.id == id) item.copy(isCompleted = !item.isCompleted) else item
        }
        val json = gson.toJson(current)
        prefs.edit().putString("amal_$todayStr", json).apply()
        _dailyAmal.value = current
    }

    // 9. Umrah Checklist
    private val _umrahChecklist = MutableStateFlow(loadUmrahChecklist())
    val umrahChecklist: StateFlow<Set<String>> = _umrahChecklist.asStateFlow()

    private fun loadUmrahChecklist(): Set<String> {
        return prefs.getStringSet("umrah_checked_items", emptySet()) ?: emptySet()
    }

    fun toggleUmrahItem(id: String) {
        val current = _umrahChecklist.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        prefs.edit().putStringSet("umrah_checked_items", current).apply()
        _umrahChecklist.value = current
    }

    // 10. Ramadan Fasting Tracker
    private val _fastingToday = MutableStateFlow(loadFastingToday())
    val fastingToday: StateFlow<Boolean> = _fastingToday.asStateFlow()

    private fun loadFastingToday(): Boolean {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        return prefs.getBoolean("ramadan_fasting_$todayStr", false)
    }

    fun toggleFastingToday() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        val next = !_fastingToday.value
        prefs.edit().putBoolean("ramadan_fasting_$todayStr", next).apply()
        _fastingToday.value = next
    }

    private val _completedJuz = MutableStateFlow(loadCompletedJuz())
    val completedJuz: StateFlow<Set<Int>> = _completedJuz.asStateFlow()

    private fun loadCompletedJuz(): Set<Int> {
        val set = prefs.getStringSet("ramadan_completed_juz", emptySet()) ?: emptySet()
        return set.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun toggleJuz(juzNumber: Int) {
        val current = _completedJuz.value.toMutableSet()
        if (current.contains(juzNumber)) {
            current.remove(juzNumber)
        } else {
            current.add(juzNumber)
        }
        prefs.edit().putStringSet("ramadan_completed_juz", current.map { it.toString() }.toSet()).apply()
        _completedJuz.value = current
    }

    // 11. Dark mode
    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("app_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        setDarkMode(!_isDarkMode.value)
    }

    fun hasUserSelectedDarkMode(): Boolean = prefs.contains("app_dark_mode")

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("app_dark_mode", enabled).apply()
        _isDarkMode.value = enabled
    }

    // 12. Prayer Alert Notification Toggles
    private val defaultPrayerAlerts = mapOf(
        "fajr" to true,
        "dhuhr" to true,
        "asr" to true,
        "maghrib" to true,
        "isha" to true,
        "tahajjud" to false,
        "ishraq" to false,
        "chasht" to false,
        "zawal" to false
    )

    private val _prayerAlerts = MutableStateFlow(loadPrayerAlerts())
    val prayerAlerts: StateFlow<Map<String, Boolean>> = _prayerAlerts.asStateFlow()

    private fun loadPrayerAlerts(): Map<String, Boolean> {
        val result = defaultPrayerAlerts.toMutableMap()
        for ((prayerId, defaultVal) in defaultPrayerAlerts) {
            val key = "prayer_alert_$prayerId"
            if (prefs.contains(key)) {
                result[prayerId] = prefs.getBoolean(key, defaultVal)
            }
        }
        return result
    }

    fun isPrayerAlertEnabled(prayerId: String): Boolean {
        return _prayerAlerts.value[prayerId] ?: defaultPrayerAlerts[prayerId] ?: false
    }

    fun setPrayerAlert(prayerId: String, enabled: Boolean) {
        prefs.edit().putBoolean("prayer_alert_$prayerId", enabled).apply()
        val current = _prayerAlerts.value.toMutableMap()
        current[prayerId] = enabled
        _prayerAlerts.value = current
    }
}
