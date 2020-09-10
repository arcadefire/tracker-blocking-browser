package org.angmarc.tracker_blocker_browser.browser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.angmarc.tracker_blocker_browser.R
import org.angmarc.tracker_blocker_browser.TestDispatcherProvider
import org.angmarc.tracker_blocker_browser.TestTrackerBlockingApplication
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomain
import org.angmarc.tracker_blocker_browser.data.database.BreakageType
import org.angmarc.tracker_blocker_browser.getValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val FULL_URL = "https://www.techcrunch.com"
private const val DOMAIN_ONLY = "techcrunch.com"

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    application = TestTrackerBlockingApplication::class
)
class BrowserViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val flowChannel = ConflatedBroadcastChannel<List<AllowedDomain>>()
    private val repository = mock<TrackersRepository> {
        runBlocking {
            on { allowedDomainsFlow() } doReturn flowChannel.asFlow()
        }
    }
    private val pageLoadProgress = PageLoadProgress()

    private val browserViewModel =
        BrowserViewModel(repository, pageLoadProgress, TestDispatcherProvider())

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should add http prefix if it's missing, when the user confirms the address`() =
        runBlockingTest {
            whenever(repository.isDomainInAllowedList(any())).doReturn(false)

            browserViewModel.addressBarText.value = "techcrunch.com"
            browserViewModel.submitAddress()

            assertThat(browserViewModel.browserState.value).isEqualTo(
                BrowserState(
                    urlToLoad = "https://techcrunch.com"
                )
            )
        }

    @Test
    fun `should not add http prefix if it's present, when the user confirms the address`() =
        runBlockingTest {
            whenever(repository.isDomainInAllowedList(any())).doReturn(false)

            browserViewModel.addressBarText.value = FULL_URL
            browserViewModel.submitAddress()

            assertThat(browserViewModel.browserState.value).isEqualTo(
                BrowserState(
                    urlToLoad = FULL_URL
                )
            )
        }

    @Test
    fun `should trim space before and after the typed address`() = runBlockingTest {
        val addressWithTrailingSpaces = "   $FULL_URL   "
        whenever(repository.isDomainInAllowedList(any())).doReturn(false)

        browserViewModel.addressBarText.value = "   $FULL_URL   "
        browserViewModel.submitAddress()

        assertThat(browserViewModel.browserState.value).isEqualTo(
            BrowserState(
                urlToLoad = "https://www.techcrunch.com",
                suspendBlockingForCurrentSite = false
            )
        )
    }

    @Test
    fun `should return the expected state when the user visits an allowed website`() =
        runBlockingTest {
            whenever(repository.isDomainInAllowedList(any())).doReturn(true)

            browserViewModel.addressBarText.value = FULL_URL
            browserViewModel.submitAddress()

            assertThat(browserViewModel.browserState.value).isEqualTo(
                BrowserState(
                    urlToLoad = FULL_URL,
                    suspendBlockingForCurrentSite = true
                )
            )
        }

    @Test
    fun `should handle the click event, when the user wants to put the current website to the allowed list`() {
        browserViewModel.addressBarText.value = FULL_URL

        browserViewModel.allowCurrentWebsite()

        val event: Event<String> = getValue(browserViewModel.allowWebsiteClicks)
        assertThat(event.peekContent()).isEqualTo(DOMAIN_ONLY)
    }


    @Test
    fun `should return a warning message when trying to add an empty url to the allowed list`() =
        runBlockingTest {
            browserViewModel.addressBarText.value = ""

            browserViewModel.allowCurrentWebsite()

            assertThat(getValue(browserViewModel.messages).peekContent()).isEqualTo(R.string.cannot_add_empty_to_allow)
        }

    @Test
    fun `should handle the click event, when the user wants to see the blocked tracker statistics`() {
        browserViewModel.addressBarText.value = FULL_URL

        browserViewModel.viewStatistics()

        val event: Event<Unit> = getValue(browserViewModel.statisticsClicks)
        assertThat(event).isNotNull
    }

    @Test
    fun `should emit a new loading state, when the progress has change from zero to an amount`() {
        pageLoadProgress.progressChanged(20)

        assertThat(getValue(browserViewModel.browserState)).isEqualTo(
            BrowserState(
                pageLoadProgress = 20
            )
        )
    }

    @Test
    fun `should emit a new loading state to hide the progress, when it reaches completion`() {
        pageLoadProgress.progressChanged(100)

        assertThat(getValue(browserViewModel.browserState)).isEqualTo(
            BrowserState(
                pageLoadProgress = 0
            )
        )
    }

    @Test
    fun `should emit a new state when the current site is flagged as an allowed domain`() =
        runBlockingTest {
            browserViewModel.browserState.observeForever {}

            browserViewModel.addressBarText.value = FULL_URL

            whenever(repository.isDomainInAllowedList(DOMAIN_ONLY)).doReturn(false)

            // Trigger an emission of a non-empty list on the allow-domains-list flow
            flowChannel.sendBlocking(
                listOf(
                    AllowedDomain(
                        DOMAIN_ONLY,
                        breakageType = BreakageType.VIDEOS_DONT_LOAD
                    )
                )
            )

            assertThat(browserViewModel.browserState.value).isEqualTo(
                BrowserState(
                    urlToLoad = "",
                    suspendBlockingForCurrentSite = true
                )
            )
        }
}