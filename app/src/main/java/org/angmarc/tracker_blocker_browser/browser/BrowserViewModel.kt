package org.angmarc.tracker_blocker_browser.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

private const val HTTP_PREFIX = "https://"

class BrowserViewModel : ViewModel() {

    val addressBarText = MutableLiveData<String>()

    val url : LiveData<String> = Transformations.map(addressBarText) {
        val address = (it ?: "").trim()
        HTTP_PREFIX + address
    }
}