package org.angmarc.tracker_blocker_browser.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance

interface ViewModelStoreOwnerBuilder<out B> {

    @BindsInstance
    fun viewModelStoreOwner(owner: ViewModelStoreOwner) : B
}
