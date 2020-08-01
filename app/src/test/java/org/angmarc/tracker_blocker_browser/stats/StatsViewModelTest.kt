package org.angmarc.tracker_blocker_browser.stats

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.angmarc.tracker_blocker_browser.data.Analytics
import org.angmarc.tracker_blocker_browser.getValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val analytics = mock<Analytics>()

    private lateinit var viewModel: StatsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())

        whenever(analytics.blockedTrackerAmount).thenReturn(10)

        viewModel = StatsViewModel(analytics)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return the amount of blocked trackers`() {
        assertThat(getValue(viewModel.blockedTrackersAmount)).isEqualTo(10)
    }
}