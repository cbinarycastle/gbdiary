package com.casoft.gbdiary.util

import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.from(this)