package org.bosf.pooch.data.api.models.scans

import com.google.gson.annotations.SerializedName
import org.bosf.pooch.data.api.models.products.ProductResponse

data class ScanResponse(
    val id: String,
    @SerializedName("dog_id") val dogId: String,
    val product: ProductResponse,
    val recommendation: String,
    @SerializedName("created_at") val createdAt: String
)