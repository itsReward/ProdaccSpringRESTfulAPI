package org.prodacc.webapi.services.dataTransferObjects

import java.time.LocalDateTime
import java.util.UUID

data class NewComment(
    val jobCardId: UUID,
    val employeeId: UUID,
    val comment: String
)

data class Comment(
    val commentId: UUID? = null,
    val jobCardId: UUID? = null,
    val jobCardName: String? = null,
    val employeeId: UUID? = null,
    val employeeName: String? = null,
    val comment: String? = null,
    val commentDate: LocalDateTime? = null
)
