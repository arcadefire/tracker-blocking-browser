package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.angmarc.tracker_blocker_browser.core.Event

private const val HTTPS_PREFIX = "https://"
private const val HTTP_PREFIX = "https://"

class BrowserViewModel : ViewModel() {

    val addressBarText = MutableLiveData<String>()
    val allowWebsiteClicks = MutableLiveData<Event<String>>()
    val statisticsClicks = MutableLiveData<Event<Unit>>()

    val url: LiveData<String> = Transformations.map(addressBarText) {
        val address = (it ?: "").trim()
        if (address.startsWith(HTTP_PREFIX) || address.startsWith(HTTPS_PREFIX)) {
            address
        } else {
            HTTPS_PREFIX + address
        }
    }

    fun allowCurrentWebsite() {
        val uri = Uri.parse(HTTP_PREFIX + addressBarText.value.orEmpty())
        if (uri.host.orEmpty().isNotBlank()) {
            allowWebsiteClicks.value =
                Event(addressBarText.value.orEmpty())
        }
    }

    fun viewStatistics() {
        statisticsClicks.value =
            Event(Unit)
    }
}