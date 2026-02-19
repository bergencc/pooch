package org.bosf.pooch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "dogs")
@TypeConverters(Converters::class)
data class Dog(
    @PrimaryKey val id: String,
    val name: String,
    val breed: String?,
    val age: Int?,
    val weight: Double?,
    val activityLevel: String?,
    val allergies: List<String>,
    val healthConditions: List<String>,
    val photoUrl: String?,
    val createdAt: String,
    val updatedAt: String
)
