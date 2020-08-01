package org.angmarc.tracker_blocker_browser.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.Database
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataAccessObject(context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "database").build()
    }

    @Singleton
    @Provides
    fun provideBlockedDomainsDao(database: Database): BlockedDomainsDao =
        database.blockedDomainsDao()

    @Singleton
    @Provides
    fun provideAllowedDomainsDao(database: Database): AllowedDomainsDao =
        database.allowedDomainsDao()
}