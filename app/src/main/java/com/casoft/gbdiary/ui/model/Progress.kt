package com.casoft.gbdiary.ui.model

sealed class Progress {

    data class InProgress(val value: Float) : Progress()

    object NotInProgress : Progress()
}