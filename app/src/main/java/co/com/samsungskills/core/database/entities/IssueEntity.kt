package co.com.samsungskills.core.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IssueEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id") val id: Int = 0,

    @ColumnInfo("siteName") val siteName: String,
    @ColumnInfo("dataTime") val dataTime: Long,
    @ColumnInfo("issueType") val issueType: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("isActive") val isActive: Boolean = true
)
