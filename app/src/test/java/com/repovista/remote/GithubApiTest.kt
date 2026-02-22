package com.repovista.remote

import com.repovista.data.remote.GithubApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class GithubApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: GithubApi

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build()))
            .build()
            .create(GithubApi::class.java)
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun `search endpoint parses response`() = kotlinx.coroutines.test.runTest {
        server.enqueue(
            MockResponse().setBody("""{"items":[{"id":1,"name":"repo","full_name":"me/repo","description":"d","stargazers_count":1,"forks_count":2,"open_issues_count":3,"language":"Kotlin","html_url":"x","owner":{"login":"me","avatar_url":"a"}}]}""")
        )

        val result = api.searchRepositories("android", 1, 20)

        assertEquals(1, result.items.size)
        assertEquals("me/repo", result.items.first().fullName)
    }
}
