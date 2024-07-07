package co.com.samsungskills.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import co.com.samsungskills.core.database.entities.SiteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {

    @Query("SELECT * FROM siteentity")
    fun getAllSite(): Flow<List<SiteEntity>>

    @Delete
    suspend fun deleteSite(siteEntity: SiteEntity)

    @Insert
    suspend fun insertSite(siteEntity: SiteEntity)

    @Insert
    suspend fun insertSiteList(siteList: List<SiteEntity>)

    @Update
    suspend fun updateSite(siteEntity: SiteEntity)

}