package com.repovista.feature.issues.data

import com.repovista.core.model.Issue
import com.repovista.core.network.dto.IssueDto
import com.repovista.core.network.mapper.DtoMapper
import java.time.Instant

object IssueMapper : DtoMapper<IssueDto, Issue> {
    override fun map(input: IssueDto): Issue = Issue(
        id = input.id,
        number = input.number,
        title = input.title,
        state = input.state,
        authorLogin = input.user.login,
        comments = input.commentsCount,
        createdAt = runCatching { Instant.parse(input.createdAt) }
            .getOrDefault(Instant.EPOCH)
    )
}
