package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.os.Build
import android.webkit.WebResourceRequest
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import org.angmarc.tracker_blocker_browser.data.Analytics
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import javax.inject.Inject

class RequestAnalyzer @Inject constructor(
    private val repository: TrackersRepository,
    private val analytics: Analytics
) {

    private lateinit var trackerSet: Set<String>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @WorkerThread
    suspend fun shouldBlockRequest(webViewUrl: String, request: WebResourceRequest): Boolean {
        return shouldBlockRequest(webViewUrl, request.url)
    }

    @WorkerThread
    suspend fun shouldBlockRequest(webViewUrl: String, requestUri: Uri): Boolean {
        val rootHost = Uri.parse(webViewUrl).host.orEmpty()
        val requestHost = requestUri.host.orEmpty()

        if (!this::trackerSet.isInitialized) {
            trackerSet = repository.trackerDomainNamesSet()
        }

        // This site has been added to the list of allowed websites
        if (repository.isDomainInAllowedList(extractDomain(rootHost))) {
            return false
        }

        // No need to block if the url being loaded is a subdomain of the one
        // typed in the address bar
        if (isSubdomain(rootHost, requestHost)) {
            return false
        }

        val isTracker = trackerSet.contains(extractDomain(requestHost))

        if (isTracker) {
            println("$requestHost is a tracker!")
            analytics.blockedTrackerAmount++
        }

        return isTracker
    }

    private fun isSubdomain(webViewUrl: String, internalUrl: String): Boolean {
        return extractDomain(webViewUrl) == extractDomain(internalUrl)
    }
}