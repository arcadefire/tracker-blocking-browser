package org.angmarc.tracker_blocker_browser.allowed_list

import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import org.angmarc.tracker_blocker_browser.di.ActivityScope
import org.angmarc.tracker_blocker_browser.di.ApplicationComponent

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [AllowDomainModule::class]
)
interface AllowDomainComponent {

    fun inject(fragment: AllowDomainFragmentDialog)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun viewModelStoreOwner(owner: ViewModelStoreOwner) : Builder

        @BindsInstance
        fun domainNameToAllow(domainName: String) : Builder

        fun applicationComponent(applicationComponent: ApplicationComponent) : Builder

        fun build() : AllowDomainComponent
    }
}