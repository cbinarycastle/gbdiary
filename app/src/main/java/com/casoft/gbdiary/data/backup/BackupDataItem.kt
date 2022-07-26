package com.casoft.gbdiary.data.backup

data class BackupDataItem(
    val day: String,
    val contents: String,
    val images: List<String>,
    val sticker: List<Int>,
)