package com.casoft.gbdiary.data.diary

enum class DiaryItemStatus(val value: Int) {
    ENABLED(1), DELETED(2)
}

fun Int.toDiaryItemStatus() = DiaryItemStatus.values().first { this == it.value }
