package com.casoft.gbdiary.util

import java.time.LocalDate
import java.time.YearMonth

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.from(this)