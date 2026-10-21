package com.muslimcompanion.app.data.model

data class KnowledgeTopic(
    val id: String,
    val category: String, // "salah", "sawm", "sunnah", "manners", "zakat"
    val titleBn: String,
    val summaryBn: String,
    val points: List<String>,
    val reference: String
)
