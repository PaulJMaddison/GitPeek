package com.repovista.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.repovista.core.database.entity.CacheMetadataEntity

@Dao
interface CacheMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(metadata: CacheMetadataEntity)

    @Query("SELECT * FROM cache_metadata WHERE `key` = :key")
    suspend fun getByKey(key: String): CacheMetadataEntity?

    @Query("DELETE FROM cache_metadata WHERE `key` = :key")
    suspend fun clearByKey(key: String)
}
