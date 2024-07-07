package co.com.samsungskills.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SiteEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id") val id: String = "",

    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("latitude") val latitude: Double = 0.0,
    @ColumnInfo("longitude") val longitude: Double = 0.0,
    @ColumnInfo("lastCheckDate") val lastCheckDate: Double = 0.0
)