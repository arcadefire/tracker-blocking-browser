package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import javax.inject.Inject

class BrowserWebClient @Inject constructor(
    private val requestAnalyzer: RequestAnalyzer,
    private val dispatcherProvider: DispatcherProvider
) : WebViewClient() {

    @WorkerThread
    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        return shouldIntercept(view, Uri.parse(url))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @WorkerThread
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return shouldIntercept(view, request.url)
    }

    @WorkerThread
    private fun shouldIntercept(
        view: WebView,
        requestUri: Uri
    ): WebResourceResponse? {
        // This callback is executed on a background thread,
        // while WebView must be accessed on its render/main thread
        return runBlocking {
            val rootUrl = withContext(dispatcherProvider.main()) {
                view.url ?: ""
            }
            if (requestAnalyzer.shouldBlockRequest(rootUrl, requestUri)) {
                WebResourceResponse(null, null, null)
            } else {
                null
            }
        }
    }
}