package org.angmarc.tracker_blocker_browser.allowed_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomain
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BreakageType
import javax.inject.Inject

class AllowDomainViewModel @Inject constructor(
    private val domainNameToAllow: String,
    private val allowedDomainsDao: AllowedDomainsDao,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    val selectedRadio: MutableLiveData<Int> = MutableLiveData(R.id.videosButton)

    private val _onAllowWebsiteAdded = MutableLiveData<Event<Unit>>()
    val onAllowWebsiteAdded: LiveData<Event<Unit>> = _onAllowWebsiteAdded

    fun addAllowedDomain() {
        CoroutineScope(dispatcherProvider.io()).launch {
            val allowedDomain = AllowedDomain(
                domain = domainNameToAllow,
                breakageType = toBreakageType(selectedRadio.value)
            )
            allowedDomainsDao.insert(allowedDomain)

            withContext(dispatcherProvider.main()) {
                _onAllowWebsiteAdded.value =
                    Event(Unit)
            }
        }
    }

    private fun toBreakageType(value: Int?): BreakageType {
        return when (value) {
            R.id.videosButton -> BreakageType.VIDEOS_DONT_LOAD
            R.id.imagesButton -> BreakageType.IMAGES_DONT_LOAD
            R.id.commentsButton -> BreakageType.MISSING_COMMENTS
            R.id.contentsButton -> BreakageType.MISSING_CONTENTS
            R.id.navigationButton -> BreakageType.BROKEN_NAVIGATION
            R.id.loginButton -> BreakageType.UNABLE_TO_LOGIN
            R.id.paywallButton -> BreakageType.PAYWALL
            else -> BreakageType.VIDEOS_DONT_LOAD
        }
    }
}