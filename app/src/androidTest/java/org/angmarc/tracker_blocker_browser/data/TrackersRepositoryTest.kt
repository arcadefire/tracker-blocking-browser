package org.angmarc.tracker_blocker_browser.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.angmarc.tracker_blocker_browser.data.database.AllowedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BlockedDomainsDao
import org.angmarc.tracker_blocker_browser.data.database.BreakageType
import org.angmarc.tracker_blocker_browser.data.database.Database
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val DOMAIN_NAME = "http://malicious-url.com"

@RunWith(AndroidJUnit4::class)
class TrackersRepositoryTest {

    private lateinit var database: Database
    private lateinit var blockedDomainsDao: BlockedDomainsDao
    private lateinit var allowedDomainsDao: AllowedDomainsDao
    private lateinit var repository: TrackersRepository

    @Before
    fun setup() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
        blockedDomainsDao = database.blockedDomainsDao()
        allowedDomainsDao = database.allowedDomainsDao()
        repository = TrackersRepository(blockedDomainsDao, allowedDomainsDao)
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun shouldStoredABlockedDomain() {
        repository.addBlockedDomain(DOMAIN_NAME)

        assertThat(repository.trackerDomainNamesSet()).isEqualTo(setOf(DOMAIN_NAME))
    }

    @Test
    fun shouldStoredAnAllowedDomain() {
        repository.addAllowedDomain(DOMAIN_NAME, breakageType = BreakageType.VIDEOS_DONT_LOAD)

        assertThat(repository.isDomainInAllowedList(DOMAIN_NAME)).isEqualTo(true)
    }

    @Test
    fun shouldReturnFalseWhenTheBlockedDomainListIsNotEmpty() {
        repository.addBlockedDomain(DOMAIN_NAME)

        assertThat(repository.isTrackerListEmpty()).isEqualTo(false)
    }

    @Test
    fun shouldReturnTrueWhenTheBlockedDomainListIsEmpty() {
        assertThat(repository.isTrackerListEmpty()).isEqualTo(true)
    }
}