package org.prodacc.webapi.models.products

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable
import java.util.*

@Entity
@Table(
    name = "product_category_reference",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_category",
            columnNames = ["product_id", "category_id"]
        )
    ]
)
@IdClass(ProductCategoryReferenceId::class)
data class ProductCategoryReference(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    val productId: Product,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "category_id", nullable = false)
    val categoryId: ProductCategory
) {
    internal constructor(): this(
        productId = Product(),
        categoryId = ProductCategory()
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductCategoryReference) return false
        return productId.id == other.productId.id &&
                categoryId.id == other.categoryId.id
    }

    override fun hashCode(): Int {
        return Objects.hash(productId.id, categoryId.id)
    }

    override fun toString(): String =
        "ProductCategoryReference(productId=${productId.id}, categoryId=${categoryId.id})"

}

data class ProductCategoryReferenceId(
    val productId: UUID? = null,
    val categoryId: UUID? = null
) : Serializable

