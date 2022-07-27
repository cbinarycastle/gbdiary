package com.casoft.gbdiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casoft.gbdiary.domain.CheckExistingSignedInUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkExistingSignedInUserUseCase: CheckExistingSignedInUserUseCase
) : ViewModel() {

    fun checkExistingSignedInUser() {
        viewModelScope.launch {
            checkExistingSignedInUserUseCase(Unit)
        }
    }
}