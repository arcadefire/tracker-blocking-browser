package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BlockedDomain::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun blockedDomainsDao(): BlockedDomainsDao
}