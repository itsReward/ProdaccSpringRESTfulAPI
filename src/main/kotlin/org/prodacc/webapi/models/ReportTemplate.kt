package org.prodacc.webapi.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.LocalDateTime.*
import java.util.UUID

@Entity
@Table(name = "report_templates")
data class ReportTemplate(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "template_id", nullable = false)
    val templateId: UUID? = null,

    @Column(name = "template_name", nullable = false)
    val templateName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    val reportType: ReportType,

    @Column(name = "configuration", columnDefinition = "JSONB")
    val configuration: String? = null,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "created_by")
    val createdBy: UUID? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = now()
)

enum class ReportType {
    PROFIT_LOSS, EXPENSE, REVENUE, INVENTORY, AGED_RECEIVABLES
}