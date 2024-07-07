package co.com.samsungskills.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import co.com.samsungskills.core.database.dao.IssueDao
import co.com.samsungskills.core.database.dao.SiteDao
import co.com.samsungskills.core.database.dao.StaffDao
import co.com.samsungskills.core.database.entities.IssueEntity
import co.com.samsungskills.core.database.entities.SiteEntity
import co.com.samsungskills.core.database.entities.StaffEntity

@Database(entities = [SiteEntity::class, IssueEntity::class, StaffEntity::class], version = 1)
abstract class MyDatabase : RoomDatabase() {

    abstract fun siteDao(): SiteDao

    abstract fun issueDao(): IssueDao

    abstract fun staffDao(): StaffDao

}