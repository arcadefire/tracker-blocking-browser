package org.angmarc.tracker_blocker_browser

import android.app.Application
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerDataFileLoader
import org.angmarc.tracker_blocker_browser.di.ApplicationComponent
import org.angmarc.tracker_blocker_browser.di.DaggerApplicationComponent
import org.angmarc.tracker_blocker_browser.workers.TrackerDataDownloadWorker
import javax.inject.Inject

class TrackerBlockingApplication : Application(), Configuration.Provider {

    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var trackerDataFileLoader: TrackerDataFileLoader

    @Inject
    lateinit var customWorkManagerConfiguration: Configuration

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
        applicationComponent.inject(this)

        workManager.enqueueUniquePeriodicWork(
            TrackerDataDownloadWorker.workerTag,
            ExistingPeriodicWorkPolicy.KEEP,
            TrackerDataDownloadWorker.workRequest()
        )

        trackerDataFileLoader.loadData()
    }

    override fun getWorkManagerConfiguration(): Configuration = customWorkManagerConfiguration
}