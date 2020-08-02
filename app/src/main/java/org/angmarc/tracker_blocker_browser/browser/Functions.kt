package org.angmarc.tracker_blocker_browser.browser

import kotlin.math.max

internal fun extractDomain(host: String, shouldStopAtDot: Boolean = false): String {
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
