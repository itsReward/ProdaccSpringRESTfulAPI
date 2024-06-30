package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
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
    @Column(name = "userRole", nullable = false, length = 15)
    open var userRole: String? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "\"employeeId\"", nullable = false)
    open var employeeId: Employee? = null

}