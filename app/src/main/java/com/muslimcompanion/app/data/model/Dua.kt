package com.muslimcompanion.app.data.model

data class DuaCategoryItem(
    val slug: String,
    val nameEn: String,
    val nameBn: String,
    val icon: String,
    val order: Int
)

data class DuaItem(
    val id: String,
    val titleBn: String,
    val categorySlug: String,
    val arabic: String,
    val transliteration: String,
    val translationBn: String,
    val reference: String,
    val source: String
)
