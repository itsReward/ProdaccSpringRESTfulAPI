package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class DeleteTechnician(
    val technicianId: UUID,
    val jobCardId: UUID
)
