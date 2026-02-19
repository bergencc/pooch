package org.bosf.pooch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "products")
@TypeConverters(Converters::class)
data class Product(
    @PrimaryKey val id: String,
    val barcode: String,
    val name: String,
    val brand: String?,
    val productType: String,
    val ingredients: List<String>,
    val nutritionInfo: Map<String, String>?,
    val ecoScore: String?,
    val photoUrl: String?,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)