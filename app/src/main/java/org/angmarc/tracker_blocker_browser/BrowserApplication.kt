package org.angmarc.tracker_blocker_browser

import android.app.Application
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerDataFileLoader
import javax.inject.Inject

class BrowserApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var trackerDataFileLoader: TrackerDataFileLoader

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
        applicationComponent.inject(this)

        trackerDataFileLoader.loadData()
    }
}