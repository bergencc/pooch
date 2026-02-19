package org.bosf.pooch.data.api.models.dogs

import com.google.gson.annotations.SerializedName

data class UpdateDogRequest(
    val name: String? = null,
    val breed: String? = null,
    val age: Int? = null,
    val weight: Double? = null,
    @SerializedName("activity_level") val activityLevel: String? = null,
    val allergies: List<String>? = null,
    @SerializedName("health_conditions") val healthConditions: List<String>? = null
)