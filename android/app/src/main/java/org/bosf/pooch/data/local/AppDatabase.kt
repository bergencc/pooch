package org.bosf.pooch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.bosf.pooch.data.local.entities.Dog
import org.bosf.pooch.data.local.entities.Product
import org.bosf.pooch.data.local.entities.ScanHistory
import org.bosf.pooch.data.local.dao.DogDao
import org.bosf.pooch.data.local.dao.ProductDao
import org.bosf.pooch.data.local.dao.ScanHistoryDao
import org.bosf.pooch.data.local.entities.Converters

@Database(
    entities = [Dog::class, Product::class, ScanHistory::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
    abstract fun productDao(): ProductDao
    abstract fun scanHistoryDao(): ScanHistoryDao
}