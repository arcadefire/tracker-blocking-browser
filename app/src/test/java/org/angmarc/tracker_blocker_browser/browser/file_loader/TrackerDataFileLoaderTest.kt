package org.angmarc.tracker_blocker_browser.browser.file_loader

import android.content.res.Resources
import com.nhaarman.mockito_kotlin.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.angmarc.tracker_blocker_browser.TestDispatcherProvider
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomain
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.angmarc.tracker_blocker_browser.data.file_loader.TrackerDataFileLoader
import org.angmarc.tracker_blocker_browser.exception_report.ExceptionEventRecorder
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.charset.StandardCharsets

@ExperimentalCoroutinesApi
internal class TrackerDataFileLoaderTest {

    private val moshi = Moshi.Builder().build()
    private val resources = mock<Resources>()
    private val blockedDomainsDao = mock<BlockedDomainsDao>()
    private val exceptionEventRecorder = mock<ExceptionEventRecorder>()

    private val trackerDataFileLoader = TrackerDataFileLoader(
        moshi,
        resources,
        blockedDomainsDao,
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
    fun shouldLoadTrackersFileData_whenSQLTableIsEmpty() {
        whenever(blockedDomainsDao.blockedDomainsNumber()).thenReturn(0)
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

        verify(blockedDomainsDao).insert(BlockedDomain("01net.com"))
    }

    @Test
    fun shouldNotLoadTrackersFileData_whenSQLTableIsNotEmpty() {
        whenever(blockedDomainsDao.blockedDomainsNumber()).thenReturn(1)

        trackerDataFileLoader.loadData()

        verify(blockedDomainsDao).blockedDomainsNumber()
        verifyNoMoreInteractions(blockedDomainsDao)
    }
}