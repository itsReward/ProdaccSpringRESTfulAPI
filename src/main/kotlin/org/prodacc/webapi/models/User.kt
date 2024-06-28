package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.util.*

@Entity
@Table(name = "users")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @ColumnDefault("username")
    @Column(name = "username", nullable = false, length = 100)
    open var username: String? = null

    @ColumnDefault("password")
    @Column(name = "password", nullable = false, length = 100)
    open var password: String? = null

    @ColumnDefault("default@email.com")
    @Column(name = "email", nullable = false, length = 100)
    open var email: String? = null

    @ColumnDefault("administrator")
    @Column(name = "userrole", nullable = false, length = 15)
    open var userrole: String? = null
}