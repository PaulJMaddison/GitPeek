package com.repovista.feature.profile.domain

import com.repovista.core.model.GitHubUser

class GetUserUseCase(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(username: String): GitHubUser =
        profileRepository.getUser(username)
}
