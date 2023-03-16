package com.casoft.gbdiary.model

import java.time.YearMonth

data class Statistics(
    val yearMonth: YearMonth,
    val stickerCounts: List<StickerCount>,
) {
    data class StickerCount(val sticker: Sticker, val count: Int)
}