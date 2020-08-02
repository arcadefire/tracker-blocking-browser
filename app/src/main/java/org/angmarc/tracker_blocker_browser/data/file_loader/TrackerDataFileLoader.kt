package org.angmarc.tracker_blocker_browser.data.file_loader

import android.content.res.Resources
import androidx.annotation.WorkerThread
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEvent
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEventRecorder
import javax.inject.Inject

class TrackerDataFileLoader @Inject constructor(
    private val moshi: Moshi,
    private val resources: Resources,
    private val repository: TrackersRepository,
    private val exceptionEventRecorder: ExceptionEventRecorder,
    dispatcherProvider: DispatcherProvider
) {

    private val scope: CoroutineScope = CoroutineScope(dispatcherProvider.io())

    @WorkerThread
    fun loadData() {
        scope.launch {
            if (!repository.isTrackerListEmpty()) {
                return@launch
            }

            try {
                val rawJson = resources.openRawResource(R.raw.tds).bufferedReader().use {
                    it.readText()
                }
                val jsonAdapter = moshi.adapter<TrackersDataFile>(TrackersDataFile::class.java)
                val trackersDataFile = jsonAdapter.fromJson(rawJson)

                if (trackersDataFile != null) {
                    trackersDataFile.trackers.entries.forEach { (domain, _) ->
                        repository.addBlockedDomain(domain)
                    }
                } else {
                    exceptionEventRecorder.record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_DISK)
                }
            } catch (exception: Exception) {
                exceptionEventRecorder.record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_DISK)
            }
        }
    }
}