package org.angmarc.tracker_blocker_browser.add_allowed_domain

import dagger.BindsInstance
import dagger.Component
import org.angmarc.tracker_blocker_browser.di.ActivityScope
import org.angmarc.tracker_blocker_browser.di.ApplicationComponent
import org.angmarc.tracker_blocker_browser.di.ViewModelStoreOwnerBuilder

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [AllowDomainModule::class]
)
interface AllowDomainComponent {

    fun inject(fragment: AllowDomainFragmentDialog)

    @Component.Builder
    interface Builder : ViewModelStoreOwnerBuilder<Builder> {

        @BindsInstance
        fun domainNameToAllow(domainName: String) : Builder

        fun applicationComponent(applicationComponent: ApplicationComponent) : Builder

        fun build() : AllowDomainComponent
    }
}