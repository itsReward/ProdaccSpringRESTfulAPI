package org.prodacc.webapi.services.dataTransferObjects

import java.util.UUID

data class NewComment(
    val jobCardId: UUID,
    val employeeId: UUID,
    val comment: String
)

data class Comment(
    val commentId: UUID? = null,
    val jobCardId: UUID? = null,
    val employeeId: UUID? = null,
    val employeeName: String? = null,
    val comment: String? = null
)
