package co.com.samsungskills.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StaffEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,

    @ColumnInfo("name") val name: String,
    @ColumnInfo("rank") val rank: String,
    @ColumnInfo("checkInOutDate") val checkInOutDate: Long? = null,
    @ColumnInfo("isCheckIn") val isCheckIn: Boolean = false
)
