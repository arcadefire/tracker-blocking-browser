package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomain
import javax.inject.Inject

private const val HTTPS_PREFIX = "https://"
private const val HTTP_PREFIX = "http://"

data class BrowserState(
    val urlToLoad: String? = null,
    val addressBarText: TextFieldValue,
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
    val allowWebsiteClicks = MutableLiveData<Event<String>>()
    val statisticsClicks = MutableLiveData<Event<Unit>>()

    val addressBarTextField = MutableLiveData<TextFieldValue>()

    val state = MediatorLiveData<BrowserState>().apply {
        addSource(addressBarTextField) { addressTextField ->
            value = BrowserState("", addressTextField, false)
        }


        addSource(allowedDomainsList) {
            val foundDomain =
                it.firstOrNull { allowedDomain ->
                    allowedDomain.domain == extractDomain(
                        addressBarTextField.value?.text
                    )
                }
            if (foundDomain != null) {
                value = BrowserState(null, TextFieldValue(), true)
            }
        }
    }

    private val _messages = MutableLiveData<Event<Int>>()
    val messages: LiveData<Event<Int>> = _messages

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    init {
        pageLoadProgress.pageLoadListener = object : PageLoadListener {
            override fun onProgress(progress: Int) {
                _loadingState.value = if (progress == 100) {
                    LoadingState(false, 0)
                } else {
                    LoadingState(true, progress)
                }
            }
        }
    }

    fun submitAddress() {
        coroutineScope.launch(dispatcherProvider.io()) {
            val urlToLoad = addressBarTextField.value?.text?.addPrefixIfNeeded().orEmpty()
            val shouldSuspendBlocking = shouldSuspendBlockingForCurrentSite(urlToLoad)

            withContext(dispatcherProvider.main()) {
                state.value = BrowserState(
                    urlToLoad,
                    addressBarTextField.value ?: TextFieldValue(),
                    shouldSuspendBlocking
                )
            }
        }
    }

    fun allowCurrentWebsite() {
        val uri = Uri.parse(addressBarTextField.value?.text.orEmpty().addPrefixIfNeeded())
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