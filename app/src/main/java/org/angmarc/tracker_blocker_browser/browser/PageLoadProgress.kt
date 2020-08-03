package org.angmarc.tracker_blocker_browser.browser

interface PageLoadListener {
    fun onProgress(progress: Int)
}

class PageLoadProgress {
    var pageLoadListener : PageLoadListener? = null

    fun progressChanged(progress: Int) {
        pageLoadListener?.onProgress(progress)
    }
}