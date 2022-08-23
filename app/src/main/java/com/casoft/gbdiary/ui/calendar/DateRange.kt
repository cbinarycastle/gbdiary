package com.casoft.gbdiary.ui.calendar

import java.time.LocalDate

class DateRange(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
) : ClosedRange<LocalDate>, Iterable<LocalDate> {

    override fun iterator(): Iterator<LocalDate> {
        return DateIterator(start, endInclusive)
    }
}

class DateIterator(
    first: LocalDate,
    private val last: LocalDate,
) : Iterator<LocalDate> {

    private var hasNext: Boolean = first <= last
    private var next = if (hasNext) first else last

    override fun hasNext(): Boolean = hasNext

    override fun next(): LocalDate {
        val value = next
        if (value == last) {
            if (!hasNext) {
                throw NoSuchElementException()
            }
            hasNext = false
        } else {
            next = next.plusDays(1)
        }
        return value
    }
}

operator fun LocalDate.rangeTo(other: LocalDate) = DateRange(this, other)