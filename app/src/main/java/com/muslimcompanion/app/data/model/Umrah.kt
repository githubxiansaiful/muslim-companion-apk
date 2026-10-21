package com.muslimcompanion.app.data.model

data class UmrahStepDua(
    val arabic: String,
    val pronunciationBn: String,
    val meaningBn: String
)

data class UmrahStep(
    val id: Int,
    val titleBn: String,
    val subtitleBn: String,
    val descBn: String,
    val duas: List<UmrahStepDua> = emptyList(),
    val tips: List<String> = emptyList()
)

data class UmrahChecklistItem(
    val id: String,
    val titleBn: String,
    val category: String,
    val isCompleted: Boolean = false
)
