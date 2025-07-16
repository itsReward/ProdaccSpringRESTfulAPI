package org.prodacc.webapi.models

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "suppliers")
data class Supplier(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "supplier_id", nullable = false)
    val supplierId: UUID? = null,

    @Column(name = "supplier_name", nullable = false)
    val supplierName: String,

    @Column(name = "company_name")
    val companyName: String? = null,

    @Column(name = "contact_person")
    val contactPerson: String? = null,

    @Column(name = "email")
    val email: String? = null,

    @Column(name = "phone")
    val phone: String? = null,

    @Column(name = "address", columnDefinition = "TEXT")
    val address: String? = null,

    @Column(name = "payment_terms")
    val paymentTerms: String? = null,

    @Column(name = "tax_number")
    val taxNumber: String? = null,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "supplier", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val products: List<Product> = emptyList()
)