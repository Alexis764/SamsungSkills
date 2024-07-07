package co.com.samsungskills.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import co.com.samsungskills.core.database.entities.StaffEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {

    @Query("SELECT * FROM StaffEntity")
    fun getAllStaff(): Flow<List<StaffEntity>>

    @Delete
    suspend fun deleteStaff(staffEntity: StaffEntity)

    @Insert
    suspend fun insertStaff(staffEntity: StaffEntity)

    @Update
    suspend fun updateStaff(staffEntity: StaffEntity)

}