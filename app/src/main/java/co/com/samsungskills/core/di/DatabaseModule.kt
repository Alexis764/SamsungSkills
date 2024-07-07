package co.com.samsungskills.core.di

import android.content.Context
import androidx.room.Room
import co.com.samsungskills.core.database.MyDatabase
import co.com.samsungskills.core.database.dao.IssueDao
import co.com.samsungskills.core.database.dao.SiteDao
import co.com.samsungskills.core.database.dao.StaffDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyDatabase {
        return Room.databaseBuilder(
            context,
            MyDatabase::class.java,
            "myDatabase"
        ).build()
    }

    @Provides
    fun provideSiteDao(myDatabase: MyDatabase): SiteDao {
        return myDatabase.siteDao()
    }

    @Provides
    fun provideIssueDao(myDatabase: MyDatabase): IssueDao {
        return myDatabase.issueDao()
    }

    @Provides
    fun provideStaffDao(myDatabase: MyDatabase): StaffDao {
        return myDatabase.staffDao()
    }

}