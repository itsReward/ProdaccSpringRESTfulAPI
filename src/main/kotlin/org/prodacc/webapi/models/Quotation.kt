package org.prodacc.webapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "quotations")
data class Quotation(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "quotation_id", nullable = false)
    val quotationId: UUID? = null,

    @Column(name = "quotation_number", nullable = false, unique = true)
    val quotationNumber: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    val client: Client,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    val vehicle: Vehicle? = null,

    @Column(name = "quotation_date", nullable = false)
    val quotationDate: java.time.LocalDate = java.time.LocalDate.now(),

    @Column(name = "valid_until")
    val validUntil: java.time.LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: QuotationStatus = QuotationStatus.DRAFT,

    @Column(name = "subtotal", precision = 12, scale = 2)
    var subtotal: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "tax_rate", precision = 5, scale = 2)
    val taxRate: java.math.BigDecimal = java.math.BigDecimal("15.00"),

    @Column(name = "tax_amount", precision = 12, scale = 2)
    var taxAmount: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "discount_percentage", precision = 5, scale = 2)
    val discountPercentage: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "discount_amount", precision = 12, scale = 2)
    var discountAmount: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "total_amount", precision = 12, scale = 2)
    var totalAmount: java.math.BigDecimal = java.math.BigDecimal.ZERO,

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    val termsAndConditions: String? = null,

    @Column(name = "converted_to_job_card")
    var convertedToJobCard: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_card_id")
    val jobCard: JobCard? = null,

    @Column(name = "created_by")
    val createdBy: UUID? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "quotation", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: List<QuotationItem> = emptyList()
)

enum class QuotationStatus {
    DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED
}

