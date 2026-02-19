package org.bosf.pooch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "scan_history")
@TypeConverters(Converters::class)
data class ScanHistory(
    @PrimaryKey val id: String,
    val dogId: String,
    val productId: String,
    val productName: String,
    val productBrand: String?,
    val productPhotoUrl: String?,
    val recommendation: String,
    val createdAt: String
)