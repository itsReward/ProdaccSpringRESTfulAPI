package org.prodacc.webapi.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "clients")
open class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "\"clientId\"", nullable = false)
    open var id: UUID? = null

    @Column(name = "\"clientName\"", nullable = false, length = 100)
    open var clientName: String? = null

    @Column(name = "\"clientSurname\"", nullable = false, length = 100)
    open var clientSurname: String? = null

    @Column(name = "gender", nullable = false, length = 100)
    open var gender: String? = null

    @Column(name = "\"jobTitle\"", nullable = false, length = 100)
    open var jobTitle: String? = null

    @Column(name = "company", nullable = false, length = 100)
    open var company: String? = null

    @Column(name = "phone", nullable = false, length = 100)
    open var phone: String? = null

    @Column(name = "email", nullable = false, length = 100)
    open var email: String? = null

    @Column(name = "address", nullable = false, length = 100)
    open var address: String? = null

    @OneToOne(mappedBy = "customerReference")
    open var jobcards: org.prodacc.webapi.models.Jobcard? = null

    @OneToMany(mappedBy = "clientReference")
    open var vehicles: MutableSet<org.prodacc.webapi.models.Vehicle> = mutableSetOf()
}