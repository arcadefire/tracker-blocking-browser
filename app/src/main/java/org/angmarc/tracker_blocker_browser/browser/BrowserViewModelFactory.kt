package org.angmarc.tracker_blocker_browser.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import javax.inject.Inject

class BrowserViewModelFactory @Inject constructor(
    private val repository: TrackersRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BrowserViewModel(repository, dispatcherProvider) as T
    }
}