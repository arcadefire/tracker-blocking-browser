package org.angmarc.tracker_blocker_browser.browser

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Module
import dagger.Provides

@Module
object BrowserActivityModule {

    @Provides
    fun provideViewModelProvider(
        viewModelOwner: ViewModelStoreOwner,
        viewModelFactory: BrowserViewModelFactory
    ): ViewModelProvider {
        return ViewModelProvider(
            viewModelOwner,
            viewModelFactory
        )
    }
}