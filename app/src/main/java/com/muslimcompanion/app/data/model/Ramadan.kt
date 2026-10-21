package com.muslimcompanion.app.data.model

data class RamadanDua(
    val id: String,
    val titleBn: String,
    val occasionBn: String,
    val arabic: String,
    val pronunciationBn: String,
    val meaningBn: String,
    val reference: String
)

data class RamadanDaySchedule(
    val dayNumber: Int,
    val dateGregorianFormatted: String,
    val sehriEndFormatted: String,
    val iftarFormatted: String,
    val isFasted: Boolean = false
)
