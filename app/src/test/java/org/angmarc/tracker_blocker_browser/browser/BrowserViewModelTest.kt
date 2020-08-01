package org.angmarc.tracker_blocker_browser.browser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.angmarc.tracker_blocker_browser.core.Event
import org.angmarc.tracker_blocker_browser.getValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BrowserViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val browserViewModel = BrowserViewModel()

    @Test
    fun `should add http prefix if it's missing, when the user confirms the address`() {
        browserViewModel.addressBarText.value = "techcrunch.com"

        assertThat(getValue(browserViewModel.url)).isEqualTo("https://techcrunch.com")
    }

    @Test
    fun `should not add http prefix if it's present, when the user confirms the address`() {
        browserViewModel.addressBarText.value = "https://techcrunch.com"

        assertThat(getValue(browserViewModel.url)).isEqualTo("https://techcrunch.com")
    }

    @Test
    fun `should trim space before and after the typed address`() {
        browserViewModel.addressBarText.value = "   https://techcrunch.com   "

        assertThat(getValue(browserViewModel.url)).isEqualTo("https://techcrunch.com")
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