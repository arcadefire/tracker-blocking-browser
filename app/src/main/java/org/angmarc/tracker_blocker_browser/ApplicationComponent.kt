package org.angmarc.tracker_blocker_browser

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        JsonModule::class,
        DatabaseModule::class
    ]
)
interface ApplicationComponent {

    fun blockedDomainsDao(): BlockedDomainsDao

    fun allowedDomainsDao(): AllowedDomainsDao

    fun dispatcherProvider(): DispatcherProvider

    fun getContext(): Context

    fun inject(application: BrowserApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}