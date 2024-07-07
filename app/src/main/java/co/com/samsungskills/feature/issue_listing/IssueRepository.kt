package co.com.samsungskills.feature.issue_listing

import co.com.samsungskills.core.database.dao.IssueDao
import co.com.samsungskills.core.database.entities.IssueEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IssueRepository @Inject constructor(
    private val issueDao: IssueDao
) {

    fun getAllIssue(): Flow<List<IssueEntity>> {
        return issueDao.getAllIssue()
    }

    suspend fun updateIssue(issueEntity: IssueEntity) {
        issueDao.updateIssue(issueEntity)
    }

}