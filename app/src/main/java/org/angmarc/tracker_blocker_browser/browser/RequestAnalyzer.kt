package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.webkit.WebResourceRequest
import androidx.annotation.WorkerThread
import org.angmarc.tracker_blocker_browser.data.Analytics
import org.angmarc.tracker_blocker_browser.data.TrackersRepository
import java.lang.Integer.max
import javax.inject.Inject

class RequestAnalyzer @Inject constructor(
    private val repository: TrackersRepository,
    private val analytics: Analytics
) {

    private val trackerSet: Set<String> by lazy {
        repository.trackerDomainNamesSet()
    }

    @WorkerThread
    fun shouldBlockRequest(webViewUrl: String, request: WebResourceRequest): Boolean {
        val rootHost = Uri.parse(webViewUrl).host.orEmpty()
        val requestHost = request.url.host.orEmpty()

        // This site has been added to the list of allowed websites
        if (repository.isDomainAllowed(rootHost)) {
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

    private fun extractDomain(host: String, shouldStopAtDot: Boolean = false): String {
        var index = host.length - 1
        var buffer = ""
        while (index >= 0 && host[index] != '.') {
            buffer = host[index] + buffer
            index--
        }
        return if (shouldStopAtDot) {
            buffer
        } else {
            extractDomain(host.substring(0, max(0, index)), true) + '.' + buffer
        }
    }

    private fun isSubdomain(webViewUrl: String, internalUrl: String): Boolean {
        return extractDomain(webViewUrl) == extractDomain(internalUrl)
    }
}