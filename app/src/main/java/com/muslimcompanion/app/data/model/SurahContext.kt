package com.muslimcompanion.app.data.model

data class SurahContext(
    val rukuCount: Int = 1,
    val namkoron: String? = null,
    val shaneNuzul: String,
    val fazilat: String,
    val tika: List<String> = emptyList()
)
