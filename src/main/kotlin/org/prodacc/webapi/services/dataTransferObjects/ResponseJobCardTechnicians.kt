package org.prodacc.webapi.services.dataTransferObjects

import java.util.*

data class ResponseJobCardTechnicians (
    val jobCardId: UUID,
    val technicianId: UUID
)