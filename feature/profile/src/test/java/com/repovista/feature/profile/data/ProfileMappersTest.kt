package com.repovista.feature.profile.data

import com.google.common.truth.Truth.assertThat
import com.repovista.core.network.dto.OwnerDto
import com.repovista.core.network.dto.RepoDto
import com.repovista.core.network.dto.UserDto
import org.junit.Test

class ProfileMappersTest {

    @Test
    fun `UserMapper maps UserDto into GitHubUser`() {
        val userDto = UserDto(
            id = 42L,
            login = "octocat",
            name = "The Octocat",
            avatarUrl = "https://avatars.githubusercontent.com/u/583231?v=4",
            htmlUrl = "https://github.com/octocat",
            bio = "GitHub mascot",
            company = "GitHub",
            location = "San Francisco",
            publicRepos = 8,
            followers = 100,
            following = 5
        )

        val result = UserMapper.map(userDto)

        assertThat(result.login).isEqualTo("octocat")
        assertThat(result.name).isEqualTo("The Octocat")
        assertThat(result.avatarUrl).isEqualTo("https://avatars.githubusercontent.com/u/583231?v=4")
        assertThat(result.followers).isEqualTo(100)
        assertThat(result.following).isEqualTo(5)
        assertThat(result.publicRepos).isEqualTo(8)
        assertThat(result.bio).isEqualTo("GitHub mascot")
    }

    @Test
    fun `RepoSummaryMapper maps RepoDto into RepoSummary`() {
        val repoDto = RepoDto(
            id = 99L,
            name = "Hello-World",
            fullName = "octocat/Hello-World",
            description = "My first repository",
            language = "Kotlin",
            stargazersCount = 80,
            forksCount = 9,
            openIssuesCount = 3,
            htmlUrl = "https://github.com/octocat/Hello-World",
            updatedAt = "2025-01-01T00:00:00Z",
            owner = OwnerDto(
                id = 1L,
                login = "octocat",
                avatarUrl = "https://avatars.githubusercontent.com/u/583231?v=4",
                htmlUrl = "https://github.com/octocat"
            )
        )

        val result = RepoSummaryMapper.map(repoDto)

        assertThat(result.id).isEqualTo(99L)
        assertThat(result.fullName).isEqualTo("octocat/Hello-World")
        assertThat(result.description).isEqualTo("My first repository")
        assertThat(result.stars).isEqualTo(80)
        assertThat(result.language).isEqualTo("Kotlin")
        assertThat(result.ownerAvatarUrl).isEqualTo("https://avatars.githubusercontent.com/u/583231?v=4")
    }
}
