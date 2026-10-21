package com.muslimcompanion.app.data.model

enum class AzkarTimeOfDay {
    MORNING,
    EVENING,
    GENERAL
}

data class ZikrItem(
    val id: String,
    val titleBn: String,
    val arabic: String,
    val transliteration: String,
    val translationBn: String,
    val countTarget: Int,
    val reference: String,
    val timeOfDay: AzkarTimeOfDay,
    val order: Int
)

data class PresetTasbih(
    val arabic: String,
    val nameBn: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val defaultTarget: Int
)
