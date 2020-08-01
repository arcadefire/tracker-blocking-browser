package org.angmarc.tracker_blocker_browser.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BlockedDomain::class, AllowedDomain::class], version = 1)
@TypeConverters(BreakageTypeConverter::class)
abstract class Database : RoomDatabase() {

    abstract fun blockedDomainsDao(): BlockedDomainsDao

    abstract fun allowedDomainsDao(): AllowedDomainsDao
}