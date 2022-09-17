package com.casoft.gbdiary.data.review

import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ReviewDataSource {

    val reviewInfo: Flow<ReviewInfo?>

    fun requestReview()

    fun consumeReviewInfo(date: LocalDate)
}