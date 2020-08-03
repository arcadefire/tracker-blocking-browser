package org.angmarc.tracker_blocker_browser.data.file_loader

import android.content.res.Resources
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
                // This coroutine is executed in the IO context, but the linter doesn't seem to detect this
                val trackersDataFile = jsonAdapter.fromJson(rawJson)!!

                trackersDataFile.trackers.entries.forEach { (domain, _) ->
                    repository.addBlockedDomain(domain)
                }
            } catch (exception: Exception) {
                exceptionEventRecorder.record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_DISK)
            }
        }
    }
}