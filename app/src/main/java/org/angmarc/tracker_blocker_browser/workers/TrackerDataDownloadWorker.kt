package org.angmarc.tracker_blocker_browser.workers

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.work.*
import kotlinx.coroutines.runBlocking
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.network.TrackersDataService
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEvent
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEventRecorder
import org.angmarc.tracker_blocker_browser.workers.core.ChildWorkerFactory
import java.util.concurrent.TimeUnit

class TrackerDataDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val trackersDataService: TrackersDataService,
    private val repository: TrackersRepository,
    private val exceptionEventRecorder: ExceptionEventRecorder
) : Worker(appContext, workerParams) {

    @WorkerThread
    override fun doWork(): Result {
        try {
            runBlocking {
                val trackerDataFile = trackersDataService.trackersDataFile()
                trackerDataFile.trackers.entries.forEach { (domain, _) ->
                    repository.addBlockedDomain(domain)
                }
            }
        } catch (exception: Exception) {
            exceptionEventRecorder.record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_REMOTE)
            return Result.failure()
        }
        return Result.success()
    }

    companion object {

        val workerTag: String =  TrackerDataDownloadWorker::class.java.simpleName

        fun workRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build()

            return PeriodicWorkRequestBuilder<TrackerDataDownloadWorker>(12, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
        }
    }

    class TrackerDataDownloadWorkerFactory(
        private val trackersDataService: TrackersDataService,
        private val repository: TrackersRepository,
        private val exceptionEventRecorder: ExceptionEventRecorder
    ) : ChildWorkerFactory {

        override fun build(
            appContext: Context,
            workerParams: WorkerParameters
        ): Worker {
            return TrackerDataDownloadWorker(
                appContext,
                workerParams,
                trackersDataService,
                repository,
                exceptionEventRecorder
            )
        }
    }
}