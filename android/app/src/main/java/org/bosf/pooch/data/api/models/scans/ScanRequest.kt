package org.bosf.pooch.data.api.models.scans

import com.google.gson.annotations.SerializedName

data class ScanRequest(
    val barcode: String,
    @SerializedName("dog_id") val dogId: String
)