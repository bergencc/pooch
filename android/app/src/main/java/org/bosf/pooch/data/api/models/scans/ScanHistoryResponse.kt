package org.bosf.pooch.data.api.models.scans

import com.google.gson.annotations.SerializedName

data class ScanHistoryResponse(
    val items: List<ScanResponse>,
    val total: Int,
    val page: Int,
    @SerializedName("per_page") val perPage: Int
)
