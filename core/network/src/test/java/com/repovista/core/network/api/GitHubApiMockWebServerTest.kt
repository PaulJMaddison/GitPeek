package com.repovista.core.network.api

import com.google.common.truth.Truth.assertThat
import com.repovista.core.network.dto.UserDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GitHubApiMockWebServerTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var gitHubApi: GitHubApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        gitHubApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GitHubApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getUser fetches and parses user profile`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {
                      "id": 583231,
                      "login": "octocat",
                      "name": "The Octocat",
                      "avatar_url": "https://avatars.githubusercontent.com/u/583231?v=4",
                      "html_url": "https://github.com/octocat",
                      "bio": "GitHub mascot",
                      "company": "GitHub",
                      "location": "San Francisco",
                      "public_repos": 8,
                      "followers": 100,
                      "following": 5
                    }
                    """.trimIndent()
                )
        )

        val user: UserDto = gitHubApi.getUser("octocat")

        val recordedRequest = mockWebServer.takeRequest()
        assertThat(recordedRequest.path).isEqualTo("/users/octocat")
        assertThat(user.login).isEqualTo("octocat")
        assertThat(user.name).isEqualTo("The Octocat")
        assertThat(user.publicRepos).isEqualTo(8)
    }
}
