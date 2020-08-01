package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BrowserWebClient @Inject constructor(
    private val trackerAnalyzer: TrackerAnalyzer
) : WebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        // This callback is executed on a background thread,
        // while WebView must be access on its render/main thread
        val rootUrl: String = runBlocking {
            withContext(Dispatchers.Main) {
                view.url ?: ""
            }
        }
        return if (trackerAnalyzer.shouldBlockRequest(rootUrl, request)) {
            WebResourceResponse(null, null, null)
        } else {
            null
        }
    }
}