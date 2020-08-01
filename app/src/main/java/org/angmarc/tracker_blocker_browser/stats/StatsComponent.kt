package org.angmarc.tracker_blocker_browser.stats

import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import org.angmarc.tracker_blocker_browser.ActivityScope
import org.angmarc.tracker_blocker_browser.ApplicationComponent

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [StatsModule::class]
)
interface StatsComponent {

    fun inject(fragment: StatsDialogFragment)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun viewModelStoreOwner(owner: ViewModelStoreOwner) : Builder

        fun applicationComponent(applicationComponent: ApplicationComponent) : Builder

        fun build() : StatsComponent
    }
}