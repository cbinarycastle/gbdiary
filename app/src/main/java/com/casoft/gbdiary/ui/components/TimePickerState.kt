package com.casoft.gbdiary.ui.components

import androidx.compose.runtime.Immutable
import com.casoft.gbdiary.ui.components.AmPm.AM
import com.casoft.gbdiary.ui.components.AmPm.PM
import java.time.LocalTime

@Immutable
data class AmPmHour(
    val amPm: AmPm,
    val hour: Int,
) {
    val hourOfDay = when {
        amPm == AM && hour == 12 -> 0
        amPm == PM && hour < 12 -> hour + 12
        else -> hour
    }
}

enum class AmPm(val text: String) {
    AM("오전"), PM("오후")
}

val LocalTime.amPmHour: AmPmHour
    get() {
        val amPm = if (hour < 12) AM else PM
        val hour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return AmPmHour(amPm, hour)
    }