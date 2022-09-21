package com.casoft.gbdiary.ui.settings

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

data class BackupAuthError(
    val exception: UserRecoverableAuthIOException,
    val action: BackupAction,
)