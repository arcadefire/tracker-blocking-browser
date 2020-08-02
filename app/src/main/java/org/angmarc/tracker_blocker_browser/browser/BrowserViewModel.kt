package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import javax.inject.Inject

private const val HTTPS_PREFIX = "https://"
private const val HTTP_PREFIX = "http://"

data class BrowserState(val urlToLoad: String, val shouldSuspendBlocking: Boolean)

class BrowserViewModel @Inject constructor(
    private val repository: TrackersRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val coroutineScope = CoroutineScope(dispatcherProvider.io())

    val addressBarText = MutableLiveData<String>()
    val allowWebsiteClicks = MutableLiveData<Event<String>>()
    val statisticsClicks = MutableLiveData<Event<Unit>>()
    val state = MediatorLiveData<BrowserState>().apply {
        addSource(addressBarText) { address ->
            coroutineScope.launch(dispatcherProvider.io()) {
                val urlToLoad = address.addPrefixIfNeeded()
                val host = Uri.parse(urlToLoad).host.orEmpty()
                val shouldSuspendBlocking = repository.isDomainInAllowedList(host)
                withContext(dispatcherProvider.main()) {
                    value = BrowserState(urlToLoad, shouldSuspendBlocking)
                }
            }
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

    private fun String.addPrefixIfNeeded(): String {
        return trim().let {
            if (it.startsWith(HTTP_PREFIX) || it.startsWith(HTTPS_PREFIX)) {
                it
            } else {
                HTTPS_PREFIX + it
            }
        }
    }
}