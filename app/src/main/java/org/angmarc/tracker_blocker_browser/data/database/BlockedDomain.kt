package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_domains")
data class BlockedDomain(
    var domain: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}