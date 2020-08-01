package org.angmarc.tracker_blocker_browser.browser

import android.net.Uri
import android.webkit.WebResourceRequest
import org.angmarc.tracker_blocker_browser.data.Analytics
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import java.lang.Integer.max
import javax.inject.Inject

class TrackerAnalyzer @Inject constructor(
    private val blockedDomainsDao: BlockedDomainsDao,
    private val allowedDomainsDao: AllowedDomainsDao,
    private val analytics: Analytics
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
        val rootHost = Uri.parse(webViewUrl).host.orEmpty()
        val requestHost = request.url.host.orEmpty()

        // This site has been whitelisted
        if (allowedDomainsDao.find(rootHost) != null) {
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