package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BreakageType {
    VIDEOS_DONT_LOAD,
    IMAGES_DONT_LOAD,
    MISSING_COMMENTS,
    MISSING_CONTENTS,
    BROKEN_NAVIGATION,
    UNABLE_TO_LOGIN,
    PAYWALL
}

@Entity(tableName = "allowed_domains")
data class AllowedDomain(
    var domain: String,
    var breakageType: BreakageType
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}