package org.angmarc.tracker_blocker_browser

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.Database
import javax.inject.Singleton

@Module
abstract class ApplicationModule {

    @Binds
    abstract fun provideContext(application: Application): Context
}

@Module
object JsonModule {

    @Provides
    fun providesResources(context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun providesMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun provideDispatcherProvider() : DispatcherProvider = DispatcherProviderImpl()
}

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