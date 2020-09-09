package org.angmarc.tracker_blocker_browser.browser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
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

    private val repository = mock<TrackersRepository> {
        runBlocking {
            on { allowedDomainsFlow() } doReturn flow { emit(emptyList<AllowedDomain>()) }
        }
    }
    private val pageLoadProgress = PageLoadProgress()

    private val browserViewModel =
        BrowserViewModel(repository, pageLoadProgress, TestDispatcherProvider())

    @Before
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
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

            assertThat(getValue(browserViewModel.browserState).urlToLoad).isEqualTo("https://techcrunch.com")
            assertThat(getValue(browserViewModel.browserState).suspendBlockingForCurrentSite).isEqualTo(false)
        }

    @Test
    fun `should not add http prefix if it's present, when the user confirms the address`() =
        runBlockingTest {
            whenever(repository.isDomainInAllowedList(any())).doReturn(false)

            browserViewModel.addressBarText.value = FULL_URL

            assertThat(getValue(browserViewModel.browserState).urlToLoad).isEqualTo("https://www.techcrunch.com")
            assertThat(getValue(browserViewModel.browserState).suspendBlockingForCurrentSite).isEqualTo(false)
        }

    @Test
    fun `should trim space before and after the typed address`() = runBlockingTest {
        whenever(repository.isDomainInAllowedList(any())).doReturn(false)

        browserViewModel.addressBarText.value = "   $FULL_URL   "

        assertThat(getValue(browserViewModel.browserState).urlToLoad).isEqualTo("https://www.techcrunch.com")
        assertThat(getValue(browserViewModel.browserState).suspendBlockingForCurrentSite).isEqualTo(false)
    }

    @Test
    fun `should return the expected state when the user visits an allowed website`() =
        runBlockingTest {
            whenever(repository.isDomainInAllowedList(any())).doReturn(true)

            browserViewModel.addressBarText.value = FULL_URL

            assertThat(getValue(browserViewModel.browserState).suspendBlockingForCurrentSite).isEqualTo(true)
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

        assertThat(getValue(browserViewModel.loadingState)).isEqualTo(
            LoadingState(
                shouldShowProgress = true,
                progress = 20
            )
        )
    }

    @Test
    fun `should emit a new loading state to hide the progress, when it reaches completion`() {
        pageLoadProgress.progressChanged(100)

        assertThat(getValue(browserViewModel.loadingState)).isEqualTo(
            LoadingState(
                shouldShowProgress = false,
                progress = 0
            )
        )
    }

    @Test
    fun `should emit a new state when the current site is flagged as an allowed domain`() =
        runBlockingTest {
            val list: MutableList<AllowedDomain> = mutableListOf()
            val flow = flow<List<AllowedDomain>> {
                while (list.isNotEmpty()) {
                    emit(list)
                }
            }

            browserViewModel.addressBarText.value = FULL_URL

            whenever(repository.isDomainInAllowedList(DOMAIN_ONLY)).doReturn(false)
            // We just take one element and unsubscribe from the infinite flow
            whenever(repository.allowedDomainsFlow()).doReturn(flow.take(1))

            // Trigger an emission of a non-empty list on the allow-domains-list flow
            list.add(AllowedDomain(DOMAIN_ONLY, breakageType = BreakageType.VIDEOS_DONT_LOAD))

            // And check the value generated from the mediator live data
            assertThat(getValue(browserViewModel.browserState).suspendBlockingForCurrentSite).isTrue()
        }
}