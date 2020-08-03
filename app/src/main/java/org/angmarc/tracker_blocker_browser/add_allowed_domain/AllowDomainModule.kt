package org.angmarc.tracker_blocker_browser.add_allowed_domain

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