package org.angmarc.tracker_blocker_browser.stats

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Module
import dagger.Provides

@Module
object StatsModule {

    @Provides
    fun provideViewModelProvider(
        viewModelOwner: ViewModelStoreOwner,
        statsViewModelFactory: StatsViewModelFactory
    ): ViewModelProvider {
        return ViewModelProvider(
            viewModelOwner,
            statsViewModelFactory
        )
    }
}