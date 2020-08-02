package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AllowedDomainsDao {

    @Query("SELECT * FROM allowed_domains")
    fun allowedDomains(): Flow<List<AllowedDomain>>

    @Query("SELECT * FROM allowed_domains WHERE domain = :url")
    fun find(url: String): AllowedDomain?

    @Insert
    fun insert(allowedDomain: AllowedDomain)
}