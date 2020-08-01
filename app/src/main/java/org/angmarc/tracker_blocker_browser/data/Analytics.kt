package org.angmarc.tracker_blocker_browser.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

private const val FILENAME = "org.angmarc.tracker_blocker"
private const val KEY_BLOCKED_TRACKERS_AMOUNT = "org.angmarc.blocked_trackers_amount"

class Analytics @Inject constructor(val context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    var blockedTrackerAmount: Int
        set(value) {
            preferences.edit { putInt(KEY_BLOCKED_TRACKERS_AMOUNT, value) }
        }
        get() = preferences.getInt(KEY_BLOCKED_TRACKERS_AMOUNT, 0)
}