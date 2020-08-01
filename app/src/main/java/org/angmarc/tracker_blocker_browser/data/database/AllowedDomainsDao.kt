package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AllowedDomainsDao {

    @Query("SELECT * FROM allowed_domains")
    fun allowedDomains(): List<AllowedDomain>

    @Insert
    fun insert(allowedDomain: AllowedDomain)
}