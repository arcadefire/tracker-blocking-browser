package org.angmarc.tracker_blocker_browser.browser

import dagger.Component
import org.angmarc.tracker_blocker_browser.di.ActivityScope
import org.angmarc.tracker_blocker_browser.di.ApplicationComponent
import org.angmarc.tracker_blocker_browser.di.ViewModelStoreOwnerBuilder

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [BrowserModule::class]
)
interface BrowserActivityComponent {

    fun inject(activity: BrowserActivity)

    @Component.Builder
    interface Builder : ViewModelStoreOwnerBuilder<Builder> {

        fun applicationComponent(applicationComponent: ApplicationComponent) : Builder

        fun build() : BrowserActivityComponent
    }
}