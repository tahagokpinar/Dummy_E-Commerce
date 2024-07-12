package com.example.afinal.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey val p_id: Long,
    val u_id: Long,
    val p_title: String,
    val p_description: String,
    val p_price: Double,
    val p_discountPercentage: Double,
    val p_rating: Double,
    val p_stock: Long,
    val p_warrantyInformation: String,
    val p_shippingInformation: String,
    val p_returnPolicy: String,
    val p_minimumOrderQuantity: Long,
    val p_images: List<String>,
    val p_thumbnail: String
) : Serializable



