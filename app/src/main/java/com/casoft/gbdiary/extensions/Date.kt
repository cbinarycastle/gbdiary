package com.casoft.gbdiary.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

val LocalDate.yearMonth
    get() = YearMonth.from(this)