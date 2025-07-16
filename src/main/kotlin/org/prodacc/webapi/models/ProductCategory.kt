package org.prodacc.webapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import org.prodacc.webapi.models.products.Product
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "product_categories")
data class ProductCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "category_id", nullable = false)
    val categoryId: UUID? = null,

    @Column(name = "category_name", nullable = false, unique = true)
    val categoryName: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val products: List<Product> = emptyList()
)