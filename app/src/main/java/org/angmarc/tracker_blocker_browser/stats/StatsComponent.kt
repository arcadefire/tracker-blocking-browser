package org.angmarc.tracker_blocker_browser.stats

import dagger.Component
import org.angmarc.tracker_blocker_browser.di.ActivityScope
import org.angmarc.tracker_blocker_browser.di.ApplicationComponent
import org.angmarc.tracker_blocker_browser.di.ViewModelStoreOwnerBuilder

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [StatsModule::class]
)
interface StatsComponent {

    fun inject(fragment: StatsDialogFragment)

    @Component.Builder
    interface Builder : ViewModelStoreOwnerBuilder<Builder> {

        fun applicationComponent(applicationComponent: ApplicationComponent) : Builder

        fun build() : StatsComponent
    }
}