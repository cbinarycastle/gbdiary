package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.review.ReviewDataSource
import com.casoft.gbdiary.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDate
import javax.inject.Inject

class ConsumeReviewInfoUseCase @Inject constructor(
    private val reviewDataSource: ReviewDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<LocalDate, Unit>(ioDispatcher) {

    override suspend fun execute(params: LocalDate) {
        reviewDataSource.consumeReviewInfo(date = params)
    }
}