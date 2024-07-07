package co.com.samsungskills

import co.com.samsungskills.core.database.dao.SiteDao
import co.com.samsungskills.core.database.dao.StaffDao
import co.com.samsungskills.core.database.entities.SiteEntity
import co.com.samsungskills.core.database.entities.StaffEntity
import co.com.samsungskills.core.local.SiteModelGson
import co.com.samsungskills.core.local.StaffModelGson
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val staffDao: StaffDao,
    private val siteDao: SiteDao
) {

    suspend fun insertSiteList(siteList: List<SiteModelGson>) {
        siteDao.insertSiteList(siteList.map {
            SiteEntity(
                it.id,
                it.name,
                it.latitude,
                it.longitude,
                it.lastCheckDate
            )
        })
    }

    suspend fun insertStaff(staffModelGson: StaffModelGson) {
        staffDao.insertStaff(
            StaffEntity(
                name = staffModelGson.name,
                rank = staffModelGson.rank
            )
        )
    }

}