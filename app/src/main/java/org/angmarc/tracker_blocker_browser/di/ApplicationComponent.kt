package org.angmarc.tracker_blocker_browser.di

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.TrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        JsonModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        WorkersModule::class
    ]
)
interface ApplicationComponent {

    fun blockedDomainsDao(): BlockedDomainsDao

    fun allowedDomainsDao(): AllowedDomainsDao

    fun dispatcherProvider(): DispatcherProvider

    fun getContext(): Context

    fun inject(application: TrackerBlockingApplication)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}