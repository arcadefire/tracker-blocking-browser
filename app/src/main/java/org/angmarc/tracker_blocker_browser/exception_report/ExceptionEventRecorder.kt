package org.angmarc.tracker_blocker_browser.exception_report

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

private const val FILENAME = "org.angmarc.tracker_blocker"

enum class ExceptionEvent {
    EXCEPTION_ON_FILE_LOAD_FROM_DISK,
    EXCEPTION_ON_FILE_LOAD_FROM_REMOTE
}

class ExceptionEventRecorder @Inject constructor(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    fun record(event: ExceptionEvent) {
        val key = event.toString()
        preferences.edit {
            putInt(key, preferences.getInt(key, 0) + 1)
        }
    }

    fun eventsCount(event: ExceptionEvent) = preferences.getInt(event.toString(), 0)
}