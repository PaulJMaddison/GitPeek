package com.repovista.data.remote

import com.repovista.data.remote.dto.GithubIssueDto
import com.repovista.data.remote.dto.GithubRepoDto
import com.repovista.data.remote.dto.GithubSearchResponseDto
import com.repovista.data.remote.dto.GithubUserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): GithubSearchResponseDto

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): GithubUserDto

    @GET("repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GithubRepoDto

    @GET("users/{username}/starred")
    suspend fun getStarredRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<GithubRepoDto>

    @GET("repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String = "open",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<GithubIssueDto>
}
