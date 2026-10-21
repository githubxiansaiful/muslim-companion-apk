package com.muslimcompanion.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Quran : Screen("quran")
    object SurahReader : Screen("surah_reader/{surahNumber}") {
        fun createRoute(surahNumber: Int) = "surah_reader/$surahNumber"
    }
    object Prayer : Screen("prayer")
    object Duas : Screen("duas")
    object Qibla : Screen("qibla")
    object Azkar : Screen("azkar")
    object Tasbih : Screen("tasbih")
    object Ramadan : Screen("ramadan")
    object Umrah : Screen("umrah")
    object Calendar : Screen("calendar")
    object Knowledge : Screen("knowledge")
    object Bookmarks : Screen("bookmarks")
    object Settings : Screen("settings")
    object More : Screen("more")
}
