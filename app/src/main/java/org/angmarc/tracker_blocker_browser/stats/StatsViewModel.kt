package org.angmarc.tracker_blocker_browser.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.angmarc.tracker_blocker_browser.data.Analytics

class StatsViewModel constructor(
    analytics: Analytics
) : ViewModel() {

    private val _blockedTrackersAmount = MutableLiveData<Int>()
    val blockedTrackersAmount: LiveData<Int> = _blockedTrackersAmount

    init {
        _blockedTrackersAmount.value = analytics.blockedTrackerAmount
    }
}