package org.angmarc.tracker_blocker_browser.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.angmarc.tracker_blocker_browser.Event

private const val HTTP_PREFIX = "https://"

class BrowserViewModel : ViewModel() {

    val addressBarText = MutableLiveData<String>()
    val allowWebsiteClicks = MutableLiveData<Event<String>>()

    val url : LiveData<String> = Transformations.map(addressBarText) {
        val address = (it ?: "").trim()
        HTTP_PREFIX + address
    }

    fun allowCurrentWebsite() {
        allowWebsiteClicks.postValue(Event(addressBarText.value.orEmpty()))
    }
}