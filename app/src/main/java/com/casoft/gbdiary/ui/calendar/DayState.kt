package com.casoft.gbdiary.ui.calendar

import com.casoft.gbdiary.model.Sticker
import org.threeten.bp.LocalDate

data class DayStateList(private val states: Map<LocalDate, DayState>) {

    operator fun get(date: LocalDate) = states[date]

    companion object {
        fun empty() = DayStateList(states = emptyMap())
    }
}

data class DayState(val sticker: Sticker?)