package org.bosf.pooch.data.api.models.products

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val id: String,
    val barcode: String,
    val name: String,
    val brand: String?,
    @SerializedName("product_type") val productType: String,
    val ingredients: List<String>,
    @SerializedName("nutrition_info") val nutritionInfo: Map<String, String>?,
    @SerializedName("eco_score") val ecoScore: String?,
    @SerializedName("photo_url") val photoUrl: String?,
    @SerializedName("created_at") val createdAt: String
)