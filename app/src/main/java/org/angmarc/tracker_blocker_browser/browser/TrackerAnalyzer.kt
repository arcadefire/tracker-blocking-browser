package org.angmarc.tracker_blocker_browser.browser

import android.webkit.WebResourceRequest
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import javax.inject.Inject

class TrackerAnalyzer @Inject constructor(
    private val blockedDomainsDao: BlockedDomainsDao
) {

    private val trackerSet: HashSet<String> by lazy {
        val trackers = blockedDomainsDao.trackerList()
        val set = HashSet<String>()
        trackers.forEach {
            set.add(it.domain)
        }
        set
    }

    fun shouldBlockRequest(webViewUrl: String, request: WebResourceRequest): Boolean {
        val host = request.url.host

        // No need to block if the url being loaded is a subdomain of the one
        // typed in the address bar
        if (host != null && isSubdomain(webViewUrl, host)) {
            return false
        }

        val isTracker = if (host != null) {
            trackerSet.contains(extractDomain(host))
        } else {
            false
        }

        if (isTracker) {
            println("$host is a tracker!")
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
            extractDomain(host.substring(0, index), true) + '.' + buffer
        }
    }

    private fun isSubdomain(webViewUrl: String, internalUrl: String): Boolean {
        return extractDomain(webViewUrl) == extractDomain(internalUrl)
    }
}