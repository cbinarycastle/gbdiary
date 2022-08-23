package com.casoft.gbdiary.notification

import androidx.annotation.StringRes
import com.casoft.gbdiary.R

enum class GBDiaryNotificationChannel(
    val id: String,
    @StringRes val nameResId: Int,
) {
    DIARY(
        id = "diary",
        nameResId = R.string.notification_channel_diary
    )
}