package org.angmarc.tracker_blocker_browser.allowed_list

import androidx.lifecycle.ViewModel

enum class Reasons {
    VIDEOS_DONT_LOAD,
    IMAGES_DONT_LOAD,
    MISSING_COMMENTS,
    MISSING_CONTENTS,
    BROKEN_NAVIGATION,
    UNABLE_TO_LOGIN,
    PAYWALL
}

class AllowListViewModel : ViewModel() {

    fun userChoseReason(reason: Reasons) {
        println(reason)
    }
}