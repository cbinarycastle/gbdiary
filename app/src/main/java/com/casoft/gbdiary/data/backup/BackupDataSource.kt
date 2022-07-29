package com.casoft.gbdiary.data.backup

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential

interface BackupDataSource {

    suspend fun backup(credential: GoogleAccountCredential)

    suspend fun sync(credential: GoogleAccountCredential)
}