package org.angmarc.tracker_blocker_browser.browser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.angmarc.tracker_blocker_browser.TestDispatcherProvider
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.getValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class BrowserViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repository = mock<TrackersRepository>()

    private val browserViewModel = BrowserViewModel(repository, TestDispatcherProvider())

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should add http prefix if it's missing, when the user confirms the address`() = runBlockingTest {
        whenever(repository.isDomainInAllowedList(any())).doReturn(false)

        browserViewModel.addressBarText.value = "techcrunch.com"

        assertThat(getValue(browserViewModel.state).urlToLoad).isEqualTo("https://techcrunch.com")
        assertThat(getValue(browserViewModel.state).shouldSuspendBlocking).isEqualTo(false)
    }

    @Test
    fun `should not add http prefix if it's present, when the user confirms the address`() = runBlockingTest {
        whenever(repository.isDomainInAllowedList(any())).doReturn(false)

        browserViewModel.addressBarText.value = "https://techcrunch.com"

        assertThat(getValue(browserViewModel.state).urlToLoad).isEqualTo("https://techcrunch.com")
        assertThat(getValue(browserViewModel.state).shouldSuspendBlocking).isEqualTo(false)
    }

    @Test
    fun `should trim space before and after the typed address`() = runBlockingTest {
        whenever(repository.isDomainInAllowedList(any())).doReturn(false)

        browserViewModel.addressBarText.value = "   https://techcrunch.com   "

        assertThat(getValue(browserViewModel.state).urlToLoad).isEqualTo("https://techcrunch.com")
        assertThat(getValue(browserViewModel.state).shouldSuspendBlocking).isEqualTo(false)
    }

    @Test
    fun `should return the expected state when the user visits an allowed website`() = runBlockingTest {
        whenever(repository.isDomainInAllowedList(any())).doReturn(true)

        browserViewModel.addressBarText.value = "https://techcrunch.com"

        assertThat(getValue(browserViewModel.state).shouldSuspendBlocking).isEqualTo(true)
    }

    @Test
    fun `should handle the click event, when the user wants to put the current website to the allowed list`() {
        browserViewModel.addressBarText.value = "https://techcrunch.com"

        browserViewModel.allowCurrentWebsite()

        val event: Event<String> = getValue(browserViewModel.allowWebsiteClicks)
        assertThat(event.peekContent()).isEqualTo("https://techcrunch.com")
    }

    @Test
    fun `should handle the click event, when the user wants to see the blocked tracker statistics`() {
        browserViewModel.addressBarText.value = "https://techcrunch.com"

        browserViewModel.viewStatistics()

        val event: Event<Unit> = getValue(browserViewModel.statisticsClicks)
        assertThat(event).isNotNull
    }
}