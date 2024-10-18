package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*

@Entity
@Table(name = "users")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @ColumnDefault("username")
    @Column(name = "username", nullable = false, length = 100)
    var username: String? = null,

    @ColumnDefault("password")
    @Column(name = "password", nullable = false, length = 100)
    var password: String? = null,

    @ColumnDefault("default@email.com")
    @Column(name = "email", nullable = false, length = 100)
    var email: String? = null,

    @ColumnDefault("administrator")
    @Column(name = "user_role", nullable = false, length = 15)
    var userRole: String? = null,

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"employee_id\"", nullable = true)
    var employeeId: Employee? = null,

    @Version
    @Column(name = "version", nullable = false)
    var version: Long? = 0,

)