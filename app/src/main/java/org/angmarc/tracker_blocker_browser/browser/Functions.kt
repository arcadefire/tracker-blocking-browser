package org.angmarc.tracker_blocker_browser.browser

internal fun extractDomain(host: String?, shouldStopAtDot: Boolean = false): String {
    if (host == null) return ""
    var index = host.length - 1
    var buffer = ""
    while (index >= 0 && host[index] != '.') {
        buffer = host[index] + buffer
        index--
    }
    return when {
        shouldStopAtDot -> buffer
        !shouldStopAtDot && index > 0 -> extractDomain(
            host.substring(0, index),
            true
        ) + '.' + buffer
        else -> ""
    }
}
