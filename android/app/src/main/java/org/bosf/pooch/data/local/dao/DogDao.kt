package org.bosf.pooch.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.bosf.pooch.data.local.entities.Dog

@Dao
interface DogDao {
    @Query("SELECT * FROM dogs ORDER BY name ASC")
    fun getAllDogs(): Flow<List<Dog>>

    @Query("SELECT * FROM dogs WHERE id = :id")
    suspend fun getDogById(id: String): Dog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDogs(dogs: List<Dog>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDog(dog: Dog)

    @Delete
    suspend fun deleteDog(dog: Dog)

    @Query("DELETE FROM dogs")
    suspend fun deleteAll()
}