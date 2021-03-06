package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BlockedDomainsDao {

    @Query("SELECT COUNT(*) from blocked_domains")
    fun blockedDomainsNumber(): Int

    @Query("SELECT * FROM blocked_domains")
    fun trackerList(): List<BlockedDomain>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(blockedDomain: BlockedDomain)
}