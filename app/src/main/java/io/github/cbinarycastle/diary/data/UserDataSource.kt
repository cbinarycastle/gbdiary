package io.github.cbinarycastle.diary.data

import io.github.cbinarycastle.diary.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    fun getUser(): Flow<User?>
}