package com.muslimcompanion.app.data.model

data class DailyAmalItem(
    val id: String,
    val titleBn: String,
    val type: String, // "prayer", "quran", "zikr", "sunnah", "charity"
    val isCompleted: Boolean = false
)
