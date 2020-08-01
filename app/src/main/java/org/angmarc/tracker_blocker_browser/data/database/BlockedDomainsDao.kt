package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BlockedDomainsDao {

    @Query("SELECT COUNT(*) from blocked_domains")
    fun blockedDomainsNumber(): Long

    @Query("SELECT * FROM blocked_domains")
    fun trackerList(): List<BlockedDomain>

    @Insert
    fun insert(blockedDomain: BlockedDomain)
}