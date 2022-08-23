package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.diary.DiaryDataSource
import com.casoft.gbdiary.data.diary.ImageDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.DiaryItem
import com.casoft.gbdiary.model.Sticker
import com.casoft.gbdiary.util.uniqueFileName
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

class SaveDiaryItemUseCase @Inject constructor(
    private val diaryDataSource: DiaryDataSource,
    private val imageDataSource: ImageDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<SaveDiaryItemUseCase.Params, Unit>(ioDispatcher) {

    override suspend fun execute(params: Params) {
        val imageFilePaths = params.images.map { image ->
            imageDataSource.copyTo(
                fileName = uniqueFileName,
                source = image.inputStream()
            ).path
        }
        diaryDataSource.save(
            DiaryItem(
                date = params.date,
                stickers = params.stickers,
                content = params.content,
                images = imageFilePaths
            )
        )
    }

    data class Params(
        val date: LocalDate,
        val stickers: List<Sticker>,
        val content: String,
        val images: List<File>,
    )
}