package com.repovista.mappers

import com.repovista.data.remote.dto.GithubRepoDto
import com.repovista.data.remote.dto.GithubUserSummaryDto
import org.junit.Assert.assertEquals
import org.junit.Test

class GithubMappersTest {
    @Test
    fun `repo dto maps to domain`() {
        val dto = GithubRepoDto(
            id = 1,
            name = "repo",
            fullName = "me/repo",
            description = "desc",
            stargazersCount = 10,
            forksCount = 2,
            openIssuesCount = 3,
            language = "Kotlin",
            htmlUrl = "http://x",
            owner = GithubUserSummaryDto("me", "avatar")
        )

        val result = dto.toDomain()
        assertEquals("me/repo", result.fullName)
        assertEquals(10, result.stars)
        assertEquals("me", result.owner.login)
    }
}
