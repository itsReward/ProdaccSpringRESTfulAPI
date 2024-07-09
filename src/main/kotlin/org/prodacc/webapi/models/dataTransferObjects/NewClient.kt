package org.prodacc.webapi.models.dataTransferObjects

data class NewClient(
    val clientName: String? = null,
    val clientSurname: String? = null,
    val gender: String? = null,
    val jobTitle: String? = null,
    val company: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)
