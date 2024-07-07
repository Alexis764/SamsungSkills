package co.com.samsungskills.feature.site_listing

import co.com.samsungskills.core.database.dao.IssueDao
import co.com.samsungskills.core.database.dao.SiteDao
import co.com.samsungskills.core.database.entities.IssueEntity
import co.com.samsungskills.core.database.entities.SiteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SiteListingRepository @Inject constructor(
    private val siteDao: SiteDao,
    private val issueDao: IssueDao
) {

    fun getAllSite(): Flow<List<SiteEntity>> {
        return siteDao.getAllSite()
    }

    suspend fun updateSite(siteEntity: SiteEntity) {
        siteDao.updateSite(siteEntity)
    }

    suspend fun insertSite(siteEntity: SiteEntity) {
        siteDao.insertSite(siteEntity)
    }

    suspend fun insertIssue(issueEntity: IssueEntity) {
        issueDao.insertIssue(issueEntity)
    }

}