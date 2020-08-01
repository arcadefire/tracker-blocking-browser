package org.angmarc.tracker_blocker_browser.di

import android.content.Context
import android.content.res.Resources
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.core.DispatcherProviderImpl
import javax.inject.Singleton

@Module
object JsonModule {

    @Provides
    fun providesResources(context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun provideDispatcherProvider() : DispatcherProvider =
        DispatcherProviderImpl()
}