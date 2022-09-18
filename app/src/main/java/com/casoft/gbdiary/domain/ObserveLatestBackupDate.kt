package com.casoft.gbdiary.domain

import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.casoft.gbdiary.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class ObserveLatestBackupDate @Inject constructor(
    private val backupDataSource: BackupDataSource,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, LocalDate?>(ioDispatcher) {

    override fun execute(params: Unit): Flow<Result<LocalDate?>> {
        return backupDataSource.latestBackupDate
            .map { Result.Success(it) }
    }
}