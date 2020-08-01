package org.angmarc.tracker_blocker_browser.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.data.Analytics
import javax.inject.Inject

class StatsViewModelFactory @Inject constructor(
    private val analytics: Analytics
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return StatsViewModel(analytics) as T
    }
}