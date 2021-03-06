package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.webkit.WebResourceRequest
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.angmarc.tracker_blocker_browser.data.Analytics
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val WEBVIEW_DOMAIN_URL = "www.duckduckgo.com"
private const val WEBVIEW_SUBDOMAIN_URL = "subdomain.duckduckgo.com"
private const val TRACKER_DOMAIN_URL = "ugly-tracker.com"

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
internal class RequestAnalyzerTest {

    private val repository = mock<TrackersRepository>()
    private val analytics = Analytics(
        context = ApplicationProvider.getApplicationContext()
    )
    private val requestAnalyzer = RequestAnalyzer(repository, analytics)

    @Test
    fun `should not block request when it is from a subdomain`() = runBlockingTest {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse(WEBVIEW_SUBDOMAIN_URL))
        whenever(repository.isDomainInAllowedList(any())).thenReturn(false)

        val shouldBlock = requestAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(shouldBlock).isFalse()
    }

    @Test
    fun `should block request when it is from a tracker`() = runBlockingTest {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse("http://www.abc.ugly-tracker.com"))
        whenever(repository.isDomainInAllowedList(any())).thenReturn(false)
        whenever(repository.trackerDomainNamesSet()).thenReturn(
            setOf(TRACKER_DOMAIN_URL)
        )

        val shouldBlock = requestAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(shouldBlock).isTrue()
    }

    @Test
    fun `should block request when it is from a tracker and it doesn't have a "www" prefix`() =
        runBlockingTest {
            val request = mock<WebResourceRequest>()
            whenever(request.url).thenReturn(Uri.parse("http://abc.ugly-tracker.com"))
            whenever(repository.isDomainInAllowedList(any())).thenReturn(false)
            whenever(repository.trackerDomainNamesSet()).thenReturn(
                setOf(TRACKER_DOMAIN_URL)
            )

            val shouldBlock = requestAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

            assertThat(shouldBlock).isTrue()
        }

    @Test
    fun `should increment the analytics counter when a tracker is blocked`() = runBlockingTest {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse("http://abc.ugly-tracker.com"))
        whenever(repository.isDomainInAllowedList(any())).thenReturn(false)
        whenever(repository.trackerDomainNamesSet()).thenReturn(
            setOf(TRACKER_DOMAIN_URL)
        )

        requestAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(analytics.blockedTrackerAmount).isEqualTo(1)
    }

    @Test
    fun `should not block if the request is coming from an allowed website`() = runBlockingTest {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse("http://abc.ugly-tracker.com"))
        whenever(repository.isDomainInAllowedList(any())).thenReturn(true)
        whenever(repository.trackerDomainNamesSet()).thenReturn(
            setOf(TRACKER_DOMAIN_URL)
        )
        whenever(repository.isDomainInAllowedList(WEBVIEW_DOMAIN_URL)).thenReturn(true)

        val shouldBlock = requestAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(shouldBlock).isFalse()
    }
}