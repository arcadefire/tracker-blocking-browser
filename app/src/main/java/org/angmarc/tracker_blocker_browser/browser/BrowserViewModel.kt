package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
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
    val urlToLoad: String? = null,
    val address: TextFieldValue,
    val suspendBlockingForCurrentSite: Boolean,
    val pageLoadProgress: Int = 0
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

    val allowWebsiteClicks = MutableLiveData<Event<String>>()
    val statisticsClicks = MutableLiveData<Event<Unit>>()
    val addressBarTextField = MutableLiveData<TextFieldValue>()

    val browserState = MediatorLiveData<BrowserState>().apply {
        addSource(addressBarTextField) { addressTextField ->
            value = BrowserState("", addressTextField, false)
        }
    }

    private val _messages = MutableLiveData<Event<Int>>()
    val messages: LiveData<Event<Int>> = _messages

    init {
        pageLoadProgress.pageLoadListener = object : PageLoadListener {
            override fun onProgress(progress: Int) {
                browserState.value = browserState.value?.copy(urlToLoad = "", pageLoadProgress = progress)
            }
        }
    }

    fun submitAddress() {
        coroutineScope.launch(dispatcherProvider.io()) {
            val urlToLoad = addressBarTextField.value?.text?.addPrefixIfNeeded().orEmpty()
            val shouldSuspendBlocking = shouldSuspendBlockingForCurrentSite(urlToLoad)

            withContext(dispatcherProvider.main()) {
                browserState.value = BrowserState(
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