package com.casoft.gbdiary.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

private const val CRASHLYTICS_KEY_PRIORITY = "priority"
private const val CRASHLYTICS_KEY_TAG = "tag"
private const val CRASHLYTICS_KEY_MESSAGE = "message"

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority <= Log.WARN) {
            return
        }

        val crashlytics = FirebaseCrashlytics.getInstance().apply {
            setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
            setCustomKey(CRASHLYTICS_KEY_TAG, tag ?: "")
            setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)
        }

        if (t == null) {
            crashlytics.recordException(Exception(message))
        } else {
            crashlytics.recordException(t)
        }
    }
}