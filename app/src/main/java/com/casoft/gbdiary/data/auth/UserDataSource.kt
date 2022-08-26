package com.casoft.gbdiary.data.auth

import com.casoft.gbdiary.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    val user: Flow<User?>

    fun checkExistingSignedInUser()
}