package org.angmarc.tracker_blocker_browser.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.angmarc.tracker_blocker_browser.core.DispatcherProvider
import org.angmarc.tracker_blocker_browser.data.database.*
import javax.inject.Inject

class TrackersRepository @Inject constructor(
    private val blockedDomainsDao: BlockedDomainsDao,
    private val allowedDomainsDao: AllowedDomainsDao,
    private val dispatcherProvider: DispatcherProvider
) {

    fun allowedDomainsFlow(): Flow<List<AllowedDomain>> = allowedDomainsDao.allowedDomains()

    suspend fun trackerDomainNamesSet(): Set<String> {
        return withContext(dispatcherProvider.io()) {
            blockedDomainsDao
                .trackerList()
                .map { it.domain }
                .toSet()
        }
    }

    suspend fun isDomainInAllowedList(rootHost: String): Boolean {
        return withContext(dispatcherProvider.io()) {
            allowedDomainsDao.find(rootHost) != null
        }
    }

    suspend fun isTrackerListEmpty(): Boolean = withContext(dispatcherProvider.io()) {
        blockedDomainsDao.blockedDomainsNumber() == 0
    }

    suspend fun addBlockedDomain(domainName: String) = withContext(dispatcherProvider.io()) {
        blockedDomainsDao.insert(BlockedDomain(domainName))
    }

    suspend fun addAllowedDomain(domainName: String, breakageType: BreakageType) = withContext(dispatcherProvider.io()) {
        allowedDomainsDao.insert(AllowedDomain(domainName, breakageType))
    }
}