package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BrowserWebClient @Inject constructor(
    private val requestAnalyzer: RequestAnalyzer
) : WebViewClient() {

    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        return shouldIntercept(view, Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return shouldIntercept(view, request.url)
    }

    private fun shouldIntercept(
        view: WebView,
        requestUri: Uri
    ): WebResourceResponse? {
        // This callback is executed on a background thread,
        // while WebView must be accessed on its render/main thread
        val rootUrl: String = runBlocking {
            withContext(Dispatchers.Main) {
                view.url ?: ""
            }
        }
        return if (requestAnalyzer.shouldBlockRequest(rootUrl, requestUri)) {
            WebResourceResponse(null, null, null)
        } else {
            null
        }
    }
}