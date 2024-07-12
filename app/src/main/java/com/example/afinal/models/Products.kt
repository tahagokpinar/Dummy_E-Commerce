package com.example.afinal.models

import java.io.Serializable

data class Products(
    val products: List<Product>,
    val total: Long,
    val skip: Long,
    val limit: Long
)

data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val category: Category,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Long,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: AvailabilityStatus,
    val returnPolicy: ReturnPolicy,
    val minimumOrderQuantity: Long,
    val images: List<String>,
    val thumbnail: String
) : Serializable

enum class AvailabilityStatus {
    InStock,
    LowStock
}

enum class Category {
    Beauty,
    Fragrances,
    Furniture,
    Groceries
}

enum class ReturnPolicy {
    NoReturnPolicy,
    The30DaysReturnPolicy,
    The60DaysReturnPolicy,
    The7DaysReturnPolicy,
    The90DaysReturnPolicy
}

