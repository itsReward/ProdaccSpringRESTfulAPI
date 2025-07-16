package org.prodacc.webapi.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "expenses")
data class Expense(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "expense_id", nullable = false)
    val expenseId: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    val category: ExpenseCategory,

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    val description: String,

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    val amount: java.math.BigDecimal,

    @Column(name = "expense_date", nullable = false)
    val expenseDate: java.time.LocalDate = java.time.LocalDate.now(),

    @Column(name = "receipt_number")
    val receiptNumber: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    val supplier: Supplier? = null,

    @Column(name = "is_recurring")
    val isRecurring: Boolean = false,

    @Column(name = "recurrence_period")
    val recurrencePeriod: String? = null,

    @Column(name = "notes", columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_by")
    val createdBy: UUID? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

