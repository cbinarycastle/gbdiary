package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.review.ReviewDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import com.google.android.play.core.review.ReviewInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveReviewInfoUseCase @Inject constructor(
    private val reviewDataSource: ReviewDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, ReviewInfo?>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<ReviewInfo?>> {
        return reviewDataSource.reviewInfo
            .map { Result.Success(it) }
    }
}