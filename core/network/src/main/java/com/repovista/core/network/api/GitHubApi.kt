package com.repovista.core.network.api

import com.repovista.core.network.dto.IssueDto
import com.repovista.core.network.dto.RepoDto
import com.repovista.core.network.dto.SearchRepositoriesResponseDto
import com.repovista.core.network.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): SearchRepositoriesResponseDto

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserDto

    @GET("users/{username}/repos")
    suspend fun listUserRepositories(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<RepoDto>

    @GET("users/{username}/starred")
    suspend fun listStarredRepositories(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<RepoDto>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetails(
        @Path("owner") owner: String,
        @Path("repo") repository: String
    ): RepoDto

    @GET("repos/{owner}/{repo}/issues")
    suspend fun listRepositoryIssues(
        @Path("owner") owner: String,
        @Path("repo") repository: String,
        @Query("state") state: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<IssueDto>
}
