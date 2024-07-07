package co.com.samsungskills.feature.staff_page

import co.com.samsungskills.core.database.dao.StaffDao
import co.com.samsungskills.core.database.entities.StaffEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StaffPageRepository @Inject constructor(
    private val staffDao: StaffDao
) {

    fun getStaff(): Flow<StaffEntity> {
        return staffDao.getAllStaff().map { it.first() }
    }

    suspend fun updateStaff(staffEntity: StaffEntity) {
        staffDao.updateStaff(staffEntity)
    }

}