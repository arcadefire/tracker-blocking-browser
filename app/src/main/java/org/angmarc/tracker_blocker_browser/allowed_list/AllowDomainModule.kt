package org.angmarc.tracker_blocker_browser.allowed_list

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dagger.Module
import dagger.Provides

@Module
object AllowDomainModule {

    @Provides
    fun provideViewModelProvider(
        viewModelOwner: ViewModelStoreOwner,
        allowDomainViewModelFactory: AllowDomainViewModelFactory
    ): ViewModelProvider {
        return ViewModelProvider(
            viewModelOwner,
            allowDomainViewModelFactory
        )
    }
}