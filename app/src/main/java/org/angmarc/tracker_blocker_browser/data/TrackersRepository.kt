package org.angmarc.tracker_blocker_browser.data

import org.angmarc.tracker_blocker_browser.data.database.*
import javax.inject.Inject

class TrackersRepository @Inject constructor(
    private val blockedDomainsDao: BlockedDomainsDao,
    private val allowedDomainsDao: AllowedDomainsDao
) {

    fun trackerDomainNamesSet() : Set<String> {
        return blockedDomainsDao
            .trackerList()
            .map { it.domain }
            .toSet()
    }

    fun isDomainAllowed(rootHost: String): Boolean {
        return allowedDomainsDao.find(rootHost) != null
    }

    fun addBlockedDomain(domainName: String) {
        blockedDomainsDao.insert(BlockedDomain(domainName))
    }

    fun addAllowedDomain(domainName: String, breakageType: BreakageType) {
        allowedDomainsDao.insert(AllowedDomain(domainName, breakageType))
    }

    fun isTrackerListEmpty(): Boolean = blockedDomainsDao.blockedDomainsNumber() == 0
}