package com.repovista.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.repovista.core.database.entity.UserRepoEntity

@Dao
interface UserRepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<UserRepoEntity>)

    @Query("DELETE FROM user_repos WHERE username = :username")
    suspend fun clearRepos(username: String)

    @Query("SELECT * FROM user_repos WHERE username = :username ORDER BY page ASC, indexInPage ASC")
    fun getReposPagingSource(username: String): PagingSource<Int, UserRepoEntity>
}
