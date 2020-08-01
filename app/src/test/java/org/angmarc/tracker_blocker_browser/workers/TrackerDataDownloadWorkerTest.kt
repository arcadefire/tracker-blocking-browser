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
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomain
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerInfo
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerOwner
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackersDataFile
import org.angmarc.tracker_blocker_browser.data.network.TrackersDataService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val TRACKER_DOMAIN_NAME = "tracker-url.com"

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class TrackerDataDownloadWorkerTest {

    private val trackerInfo = TrackerInfo("a domain", TrackerOwner("owner", "display name"))
    private val workerParameters = mock<WorkerParameters>()
    private val trackersDataService = mock<TrackersDataService>()
    private val blockedDomainsDao = mock<BlockedDomainsDao>()

    private val worker = TrackerDataDownloadWorker(
        ApplicationProvider.getApplicationContext(),
        workerParameters,
        trackersDataService,
        blockedDomainsDao
    )

    @Test
    fun `should persist the refreshed data file contents and return success`() {
        runBlockingTest {
            whenever(trackersDataService.trackersDataFile())
                .thenReturn(TrackersDataFile(mapOf(TRACKER_DOMAIN_NAME to trackerInfo)))
            val result = worker.doWork()

            verify(blockedDomainsDao).insert(BlockedDomain(TRACKER_DOMAIN_NAME))
            assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }
    }

    @Test
    fun `should fail gracefully when an exception is thrown by the service and return failure`() {
        runBlockingTest {
            whenever(trackersDataService.trackersDataFile()).thenThrow()

            val result = worker.doWork()

            verifyNoMoreInteractions(blockedDomainsDao)
            assertThat(result).isEqualTo(ListenableWorker.Result.failure())
        }
    }
}