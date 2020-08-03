package org.angmarc.tracker_blocker_browser.browser

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Module
import dagger.Provides
import org.angmarc.tracker_blocker_browser.di.ActivityScope

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

    @ActivityScope
    @Provides
    fun providePageLoadProgress() : PageLoadProgress = PageLoadProgress()
}