package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.LocalImage
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetLocalImagesUseCase @Inject constructor(
    private val imageDataSource: ImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Unit, List<LocalImage>>(ioDispatcher) {

    override suspend fun execute(params: Unit): List<LocalImage> =
        imageDataSource.getLocalImages()
}