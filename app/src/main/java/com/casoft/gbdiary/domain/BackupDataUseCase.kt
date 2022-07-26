package com.casoft.gbdiary.domain

import android.accounts.Account
import android.content.Context
import com.casoft.gbdiary.data.backup.BackupDataSource
import com.casoft.gbdiary.di.IoDispatcher
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class BackupDataUseCase @Inject constructor(
    private val backupDataSource: BackupDataSource,
    @ApplicationContext private val context: Context,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : UseCase<Account, Unit>(ioDispatcher) {

    override suspend fun execute(params: Account) {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(DriveScopes.DRIVE_APPDATA)
        ).apply { selectedAccount = params }

        backupDataSource.backup(credential)
    }
}