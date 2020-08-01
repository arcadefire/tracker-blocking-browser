package org.angmarc.tracker_blocker_browser.data.network

import org.angmarc.tracker_blocker_browser.data.file_loader.TrackersDataFile
import retrofit2.http.GET

interface TrackersDataService {

    @GET("https://staticcdn.duckduckgo.com/trackerblocking/v2.1/tds.json")
    suspend fun trackersDataFile(): TrackersDataFile
}