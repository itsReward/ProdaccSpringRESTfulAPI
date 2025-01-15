package org.prodacc.webapi.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "clients")
data class Client (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"clientId\"", nullable = false)
    var id: UUID? = null,

    @Column(name = "\"clientName\"", nullable = false, length = 100)
    var clientName: String = "not_set",

    @Column(name = "\"clientSurname\"", nullable = false, length = 100)
    var clientSurname: String = "not_set",

    @Column(name = "gender", nullable = false, length = 100)
    var gender: String = "not_set",

    @Column(name = "\"jobTitle\"", nullable = true, length = 100)
    var jobTitle: String? = "not_set",

    @Column(name = "company", nullable = true, length = 100)
    var company: String? = "not_set",

    @Column(name = "phone", nullable = false, length = 100)
    var phone: String = "not_set",

    @Column(name = "email", nullable = true, length = 100)
    var email: String? = "not@set",

    @Column(name = "address", nullable = true, length = 100)
    var address: String? = "not_set",

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = 0,

    @OneToMany(mappedBy = "customerReference", cascade = [CascadeType.ALL], orphanRemoval = true)
    var jobCards: List<JobCard> = listOf(),

    @OneToMany(mappedBy = "clientReference", cascade = [CascadeType.ALL], orphanRemoval = true)
    var vehicles: List<Vehicle> = listOf()
)