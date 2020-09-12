package org.angmarc.tracker_blocker_browser.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.angmarc.tracker_blocker_browser.data.network.TrackersDataService
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEventRecorder
import org.angmarc.tracker_blocker_browser.workers.TrackerDataDownloadWorker
import org.angmarc.tracker_blocker_browser.workers.core.ChildWorkerFactory
import org.angmarc.tracker_blocker_browser.workers.core.CustomWorkerFactory
import javax.inject.Singleton

@Module
object WorkersModule {

    @Provides
    @IntoMap
    @StringKey("org.angmarc.tracker_blocker_browser.workers.TrackerDataDownloadWorker")
    fun provideWorkerFactory(
        trackersDataService: TrackersDataService,
        repository: TrackersRepository,
        exceptionEventRecorder: ExceptionEventRecorder
    ): ChildWorkerFactory {
        return TrackerDataDownloadWorker.TrackerDataDownloadWorkerFactory(
            trackersDataService,
            repository,
            exceptionEventRecorder
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager = WorkManager.getInstance(context)

    @Provides
    fun provideWorkManagerConfiguration(
        customWorkerFactory: CustomWorkerFactory
    ) : Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(customWorkerFactory)
            .build()
    }
}