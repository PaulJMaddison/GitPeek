package com.repovista.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.repovista.core.database.dao.CacheMetadataDao
import com.repovista.core.database.dao.SearchRepoDao
import com.repovista.core.database.dao.UserRepoDao
import com.repovista.core.database.entity.CacheMetadataEntity
import com.repovista.core.database.entity.SearchRepoEntity
import com.repovista.core.database.entity.UserRepoEntity

@Database(
    entities = [SearchRepoEntity::class, UserRepoEntity::class, CacheMetadataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun searchRepoDao(): SearchRepoDao
    abstract fun userRepoDao(): UserRepoDao
    abstract fun cacheMetadataDao(): CacheMetadataDao
}
