package org.angmarc.tracker_blocker_browser.data.file_loader

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackersDataFile(
    val trackers: Map<String, TrackerInfo>
)

@JsonClass(generateAdapter = true)
data class TrackerInfo(
    val domain: String,
    val owner: TrackerOwner
)

@JsonClass(generateAdapter = true)
data class TrackerOwner(
    val name: String,
    val displayName: String
)

