package io.github.cbinarycastle.diary.data.auth

import io.github.cbinarycastle.diary.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    val user: Flow<User?>
}