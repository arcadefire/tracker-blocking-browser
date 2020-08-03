package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebChromeClient
import android.webkit.WebView
import javax.inject.Inject

class BrowserWebChomeClient @Inject constructor(
    private val pageLoadProgress: PageLoadProgress
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)

        pageLoadProgress.progressChanged(newProgress)
    }
}