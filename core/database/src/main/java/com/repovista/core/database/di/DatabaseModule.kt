package com.repovista.core.database.di

import android.content.Context
import androidx.room.Room
import com.repovista.core.database.AppDatabase
import com.repovista.core.database.dao.CacheMetadataDao
import com.repovista.core.database.dao.SearchRepoDao
import com.repovista.core.database.dao.UserRepoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "repovista.db").build()

    @Provides
    fun provideSearchRepoDao(database: AppDatabase): SearchRepoDao = database.searchRepoDao()

    @Provides
    fun provideUserRepoDao(database: AppDatabase): UserRepoDao = database.userRepoDao()

    @Provides
    fun provideCacheMetadataDao(database: AppDatabase): CacheMetadataDao = database.cacheMetadataDao()
}
