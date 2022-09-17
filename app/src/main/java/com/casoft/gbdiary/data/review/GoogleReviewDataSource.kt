package com.casoft.gbdiary.data.review

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.casoft.gbdiary.data.diary.DiaryItemDao
import com.casoft.gbdiary.di.PreferencesKeys
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.Period

class GoogleReviewDataSource(
    private val preferencesDataStore: DataStore<Preferences>,
    diaryItemDao: DiaryItemDao,
    private val reviewManager: ReviewManager,
    private val externalScope: CoroutineScope,
) : ReviewDataSource {

    private val _reviewInfo = MutableStateFlow<ReviewInfo?>(null)
    override val reviewInfo: Flow<ReviewInfo?> = _reviewInfo.asStateFlow()

    private val latestLaunchReviewFlowDate: StateFlow<LocalDate?> =
        preferencesDataStore.data
            .map { prefs ->
                prefs[PreferencesKeys.LATEST_LAUNCH_REVIEW_FLOW_DATE]?.let { epochDay ->
                    LocalDate.ofEpochDay(epochDay)
                }
            }
            .stateIn(
                scope = externalScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    private val totalDiaryItems: StateFlow<Int> =
        diaryItemDao.countStream()
            .stateIn(
                scope = externalScope,
                started = SharingStarted.Eagerly,
                initialValue = 0
            )

    override fun requestReview() {
        externalScope.launch {
            try {
                val latestLaunchDate = latestLaunchReviewFlowDate.value
                if (latestLaunchDate == null) {
                    if (totalDiaryItems.value >= 3) {
                        _reviewInfo.value = reviewManager.requestReview()
                    }
                } else {
                    val periodFromLatestLaunchDate =
                        Period.between(latestLaunchDate, LocalDate.now())
                    if (periodFromLatestLaunchDate.days > 7) {
                        _reviewInfo.value = reviewManager.requestReview()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    override fun consumeReviewInfo(date: LocalDate) {
        _reviewInfo.value = null

        externalScope.launch {
            preferencesDataStore.edit { prefs ->
                prefs[PreferencesKeys.LATEST_LAUNCH_REVIEW_FLOW_DATE] = date.toEpochDay()
            }
        }
    }
}