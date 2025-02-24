package org.prodacc.webapi.models.products

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "product_category")
data class ProductCategory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    val id: UUID,

    @Column(name = "\"name\"", nullable = false, length = 100)
    val name: String,

    @Column(name = "\"description\"", nullable = true, length = 100)
    val description: String,

    @OneToMany(mappedBy = "categoryId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val productReference: MutableSet<ProductCategoryReference> = mutableSetOf()

){
    internal constructor(): this(
        id = UUID.randomUUID(),
        name = "",
        description = "",
        productReference = mutableSetOf()
    )

    override fun toString(): String = "ProductCategory(id=$id, name='$name')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductCategory) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

}