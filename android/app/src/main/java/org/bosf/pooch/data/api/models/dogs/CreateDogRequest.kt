package org.bosf.pooch.data.api.models.dogs

import com.google.gson.annotations.SerializedName

data class CreateDogRequest(
    val name: String,
    val breed: String? = null,
    val age: Int? = null,
    val weight: Double? = null,
    @SerializedName("activity_level") val activityLevel: String? = null,
    val allergies: List<String> = emptyList(),
    @SerializedName("health_conditions") val healthConditions: List<String> = emptyList()
)