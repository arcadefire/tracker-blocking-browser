package org.angmarc.tracker_blocker_browser.workers

import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.angmarc.tracker_blocker_browser.TestTrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerInfo
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerOwner
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackersDataFile
import org.angmarc.tracker_blocker_browser.data.network.TrackersDataService
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEvent
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEventRecorder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val TRACKER_DOMAIN_NAME = "tracker-url.com"

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest= Config.NONE,
    application = TestTrackerBlockingApplication::class
)
class TrackerDataDownloadWorkerTest {

    private val trackerInfo = TrackerInfo("a domain", TrackerOwner("owner", "display name"))
    private val workerParameters = mock<WorkerParameters>()
    private val trackersDataService = mock<TrackersDataService>()
    private val repository = mock<TrackersRepository>()
    private val exceptionEventRecorder = mock<ExceptionEventRecorder>()

    private val worker = TrackerDataDownloadWorker(
        ApplicationProvider.getApplicationContext(),
        workerParameters,
        trackersDataService,
        repository,
        exceptionEventRecorder
    )

    @Test
    fun `should persist the refreshed data file contents and return success`() {
        runBlockingTest {
            whenever(trackersDataService.trackersDataFile())
                .thenReturn(TrackersDataFile(mapOf(TRACKER_DOMAIN_NAME to trackerInfo)))
            val result = worker.doWork()

            verify(repository).addBlockedDomain(TRACKER_DOMAIN_NAME)
            assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }
    }

    @Test
    fun `should fail gracefully when an exception is thrown by the service and return failure`() {
        runBlockingTest {
            whenever(trackersDataService.trackersDataFile()).thenThrow()

            val result = worker.doWork()

            verifyNoMoreInteractions(repository)
            verify(exceptionEventRecorder).record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_REMOTE)
            assertThat(result).isEqualTo(ListenableWorker.Result.failure())
        }
    }
}