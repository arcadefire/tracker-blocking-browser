package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomain
import javax.inject.Inject

private const val HTTPS_PREFIX = "https://"
private const val HTTP_PREFIX = "http://"

data class BrowserState(
    val urlToLoad: String? = null,
    val shouldSuspendBlocking: Boolean
)

data class LoadingState(
    val shouldShowProgress: Boolean,
    val progress: Int
)

class BrowserViewModel @Inject constructor(
    private val repository: TrackersRepository,
    pageLoadProgress: PageLoadProgress,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val coroutineScope = CoroutineScope(dispatcherProvider.io())
    private val allowedDomainsList = liveData<List<AllowedDomain>> {
        emitSource(repository.allowedDomainsFlow().asLiveData())
    }
    val loadingState = MutableLiveData<LoadingState>()

    val addressBarText = MutableLiveData<String>()
    val allowWebsiteClicks = MutableLiveData<Event<String>>()
    val statisticsClicks = MutableLiveData<Event<Unit>>()

    val state = MediatorLiveData<BrowserState>().apply {
        addSource(addressBarText) { address ->
            coroutineScope.launch(dispatcherProvider.io()) {
                val urlToLoad = address.addPrefixIfNeeded()
                val shouldSuspendBlocking = shouldSuspendBlockingForCurrentSite(urlToLoad)

                withContext(dispatcherProvider.main()) {
                    value = BrowserState(urlToLoad, shouldSuspendBlocking)
                }
            }
        }
        addSource(allowedDomainsList) {
            val foundDomain =
                it.firstOrNull { allowedDomain -> allowedDomain.domain == addressBarText.value }
            if (foundDomain != null) {
                value = BrowserState(null, true)
            }
        }
    }

    init {
        pageLoadProgress.pageLoadListener = object : PageLoadListener {
            override fun onProgress(progress: Int) {
                loadingState.value = if (progress == 100) {
                    LoadingState(false, 0)
                } else {
                    LoadingState(true, progress)
                }
            }
        }
    }

    fun allowCurrentWebsite() {
        val uri = Uri.parse(addressBarText.value.orEmpty().addPrefixIfNeeded())
        val domain = extractDomain(uri.host.orEmpty())
        if (domain.isNotBlank()) {
            allowWebsiteClicks.value = Event(domain)
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

    private suspend fun shouldSuspendBlockingForCurrentSite(urlToLoad: String): Boolean {
        val host = Uri.parse(urlToLoad).host.orEmpty()
        val domain = extractDomain(host)
        return repository.isDomainInAllowedList(domain)
    }
}