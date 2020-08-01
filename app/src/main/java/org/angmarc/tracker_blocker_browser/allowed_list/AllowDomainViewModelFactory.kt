package org.angmarc.tracker_blocker_browser.allowed_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import javax.inject.Inject

class AllowDomainViewModelFactory @Inject constructor(
    private val allowedDomainsDao: AllowedDomainsDao,
    private val domainNameToAllow: String,
    private val dispatcherProvider: DispatcherProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllowDomainViewModel(domainNameToAllow, allowedDomainsDao, dispatcherProvider) as T
    }
}