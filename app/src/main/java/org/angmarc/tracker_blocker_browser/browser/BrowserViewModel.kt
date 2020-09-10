package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import javax.inject.Inject

private const val HTTPS_PREFIX = "https://"
private const val HTTP_PREFIX = "http://"

data class BrowserState(
    val urlToLoad: String = "",
    val suspendBlockingForCurrentSite: Boolean = false,
    val pageLoadProgress: Int = 0
)

class BrowserViewModel @Inject constructor(
    private val repository: TrackersRepository,
    pageLoadProgress: PageLoadProgress,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val coroutineScope = CoroutineScope(dispatcherProvider.io())

    val allowWebsiteClicks = MutableLiveData<Event<String>>()
    val statisticsClicks = MutableLiveData<Event<Unit>>()
    val addressBarText = MutableLiveData<String>()
    val browserState = MutableLiveData<BrowserState>()

    private val _messages = MutableLiveData<Event<Int>>()
    val messages: LiveData<Event<Int>> = _messages

    init {
        pageLoadProgress.pageLoadListener = object : PageLoadListener {
            override fun onProgress(progress: Int) {
                val state = browserState.value
                val loadProgress = if (progress == 100) 0 else progress
                browserState.value =
                    state?.copy(urlToLoad = "", pageLoadProgress = loadProgress)
                        ?: BrowserState(pageLoadProgress = loadProgress)
            }
        }

        coroutineScope.launch(dispatcherProvider.io()) {
            repository
                .allowedDomainsFlow()
                .onEach {
                    val foundDomain =
                        it.firstOrNull { allowedDomain ->
                            allowedDomain.domain == extractDomain(addressBarText.value)
                        }
                    if (foundDomain != null) {
                        withContext(dispatcherProvider.main()) {
                            val state = browserState.value ?: BrowserState()
                            browserState.value = state.copy(suspendBlockingForCurrentSite = true)
                        }
                    }
                }
                .collect()
        }
    }

    fun submitAddress() {
        coroutineScope.launch(dispatcherProvider.io()) {
            val urlToLoad = addressBarText.value?.addPrefixIfNeeded().orEmpty()
            val shouldSuspendBlocking = shouldSuspendBlockingForCurrentSite(urlToLoad)

            withContext(dispatcherProvider.main()) {
                browserState.value = BrowserState(
                    urlToLoad,
                    shouldSuspendBlocking
                )
            }
        }
    }

    fun allowCurrentWebsite() {
        val uri = Uri.parse(addressBarText.value.orEmpty().addPrefixIfNeeded())
        val domain = extractDomain(uri.host.orEmpty())
        if (domain.isNotBlank()) {
            allowWebsiteClicks.value = Event(domain)
        } else {
            _messages.value = Event(R.string.cannot_add_empty_to_allow)
        }
    }

    fun viewStatistics() {
        statisticsClicks.value = Event(Unit)
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

    private suspend fun shouldSuspendBlockingForCurrentSite(urlToLoad: String): Boolean {
        val host = Uri.parse(urlToLoad).host.orEmpty()
        val domain = extractDomain(host)
        return repository.isDomainInAllowedList(domain)
    }
}