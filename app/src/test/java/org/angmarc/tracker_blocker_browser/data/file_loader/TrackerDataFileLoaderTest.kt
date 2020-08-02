package org.angmarc.tracker_blocker_browser.browser.file_loader

import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import com.nhaarman.mockito_kotlin.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.angmarc.tracker_blocker_browser.TestDispatcherProvider
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerDataFileLoader
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEvent
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEventRecorder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets

@ExperimentalCoroutinesApi
internal class TrackerDataFileLoaderTest {

    private val moshi = Moshi.Builder().build()
    private val resources = mock<Resources>()
    private val repository = mock<TrackersRepository>()
    private val exceptionEventRecorder = mock<ExceptionEventRecorder>()

    private val trackerDataFileLoader = TrackerDataFileLoader(
        moshi,
        resources,
        repository,
        exceptionEventRecorder,
        TestDispatcherProvider()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should load the trackers data file from disk at the app's boot, when the storage is empty`() =
        runBlockingTest {
            whenever(repository.isTrackerListEmpty()).thenReturn(true)
            whenever(resources.openRawResource(any())).thenReturn(
                """
                {
                    "trackers": {
                        "01net.com": {
                            "domain": "01net.com",
                            "owner": {
                                "name": "NextInteractive",
                                "displayName": "NextInteractive"
                            },
                            "prevalence": 0.000152,
                            "fingerprinting": 1,
                            "cookies": 0.000139,
                            "categories": [],
                            "default": "ignore"
                        }
                    }
                }
            """.trimIndent().byteInputStream(StandardCharsets.UTF_8)
            )

            trackerDataFileLoader.loadData()

            verify(repository).addBlockedDomain("01net.com")
        }

    @Test
    fun `should not load anything at the app's boot, when the storage contains trackers definitions`() =
        runBlockingTest {
            whenever(repository.isTrackerListEmpty()).thenReturn(false)

            trackerDataFileLoader.loadData()

            verify(repository).isTrackerListEmpty()
            verifyNoMoreInteractions(repository)
        }

    @Test
    fun `should record the exception while loading the trackers file from the disk`() =
        runBlockingTest {
            whenever(repository.isTrackerListEmpty()).thenReturn(true)
            whenever(resources.openRawResource(any())).thenThrow(NotFoundException())

            trackerDataFileLoader.loadData()

            verify(exceptionEventRecorder).record(ExceptionEvent.EXCEPTION_ON_FILE_LOAD_FROM_DISK)
        }
}