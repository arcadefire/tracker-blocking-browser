package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.webkit.WebResourceRequest
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomain
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

private const val WEBVIEW_DOMAIN_URL = "www.duckduckgo.com"
private const val WEBVIEW_SUBDOMAIN_URL = "subdomain.duckduckgo.com"
private const val TRACKER_DOMAIN_URL = "ugly-tracker.com"

internal class TrackerAnalyzerTest {

    private val blockedDomainsDao = mock<BlockedDomainsDao>()
    private val trackerAnalyzer = TrackerAnalyzer(blockedDomainsDao)

    @Test
    fun shouldNotBlockRequest_whenSubdomainIsSameAsWebViewUrl() {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse(WEBVIEW_SUBDOMAIN_URL))

        val shouldBlock = trackerAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(shouldBlock).isFalse()
    }

    @Test
    fun shouldBlockRequest_whenUrlIsATracker() {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse("http://www.abc.ugly-tracker.com"))
        whenever(blockedDomainsDao.trackerList()).thenReturn(
            listOf(BlockedDomain(domain = TRACKER_DOMAIN_URL))
        )

        val shouldBlock = trackerAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(shouldBlock).isTrue()
    }

    @Test
    fun shouldBlockRequest_whenUrlIsWithoutWWWPrefixAndATracker() {
        val request = mock<WebResourceRequest>()
        whenever(request.url).thenReturn(Uri.parse("http://abc.ugly-tracker.com"))
        whenever(blockedDomainsDao.trackerList()).thenReturn(
            listOf(BlockedDomain(domain = TRACKER_DOMAIN_URL))
        )

        val shouldBlock = trackerAnalyzer.shouldBlockRequest(WEBVIEW_DOMAIN_URL, request)

        assertThat(shouldBlock).isTrue()
    }
}