package co.com.samsungskills.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import co.com.samsungskills.core.database.entities.IssueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IssueDao {

    @Query("SELECT * FROM IssueEntity")
    fun getAllIssue(): Flow<List<IssueEntity>>

    @Delete
    suspend fun deleteIssue(issueEntity: IssueEntity)

    @Insert
    suspend fun insertIssue(issueEntity: IssueEntity)

    @Update
    suspend fun updateIssue(issueEntity: IssueEntity)

}