package org.bosf.pooch.data.api.models.dogs

import com.google.gson.annotations.SerializedName

data class DogResponse(
    val id: String,
    val name: String,
    val breed: String?,
    val age: Int?,
    val weight: Double?,
    @SerializedName("activity_level") val activityLevel: String?,
    val allergies: List<String>,
    @SerializedName("health_conditions") val healthConditions: List<String>,
    @SerializedName("photo_url") val photoUrl: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)