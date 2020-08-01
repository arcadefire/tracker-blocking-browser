package org.angmarc.tracker_blocker_browser.di

import dagger.Module
import dagger.Provides
import org.angmarc.tracker_blocker_browser.data.network.TrackersDataService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://duckduckgo.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideTrackerDataService(retrofit: Retrofit) : TrackersDataService =
        retrofit.create(TrackersDataService::class.java)
}
