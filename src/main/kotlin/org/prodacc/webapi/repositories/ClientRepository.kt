package org.prodacc.webapi.repositories

import org.prodacc.webapi.models.Client
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ClientRepository: CrudRepository<Client, UUID> {
}