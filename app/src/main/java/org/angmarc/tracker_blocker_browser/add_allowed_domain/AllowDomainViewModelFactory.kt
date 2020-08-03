package org.angmarc.tracker_blocker_browser.add_allowed_domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import javax.inject.Inject

class AllowDomainViewModelFactory @Inject constructor(
    private val repository: TrackersRepository,
    private val domainNameToAllow: String,
    private val dispatcherProvider: DispatcherProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllowDomainViewModel(domainNameToAllow, repository, dispatcherProvider) as T
    }
}