package com.repovista.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.repovista.core.database.entity.SearchRepoEntity

@Dao
interface SearchRepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<SearchRepoEntity>)

    @Query("DELETE FROM search_repos WHERE `query` = :query")
    suspend fun clearRepos(query: String)

    @Query("SELECT * FROM search_repos WHERE `query` = :query ORDER BY page ASC, indexInPage ASC")
    fun getReposPagingSource(query: String): PagingSource<Int, SearchRepoEntity>
}
