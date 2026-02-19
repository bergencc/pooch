package org.bosf.pooch.data.repository

import kotlinx.coroutines.flow.Flow
import org.bosf.pooch.data.api.ApiService
import org.bosf.pooch.data.api.models.dogs.CreateDogRequest
import org.bosf.pooch.data.api.models.dogs.DogResponse
import org.bosf.pooch.data.api.models.dogs.UpdateDogRequest
import org.bosf.pooch.data.local.dao.DogDao
import org.bosf.pooch.data.local.entities.Dog
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DogRepository @Inject constructor(
    private val api: ApiService,
    private val dogDao: DogDao
) {
    fun getAllDogs(): Flow<List<Dog>> = dogDao.getAllDogs()

    suspend fun refreshDogs(): NetworkResult<List<DogResponse>> {
        val result = safeApiCall { api.getDogs() }

        if (result is NetworkResult.Success) {
            val entities = result.data.map { it.toEntity() }

            dogDao.deleteAll()
            dogDao.insertDogs(entities)
        }

        return result
    }

    suspend fun createDog(request: CreateDogRequest): NetworkResult<DogResponse> {
        val result = safeApiCall { api.createDog(request) }

        if (result is NetworkResult.Success) {
            dogDao.insertDog(result.data.toEntity())
        }

        return result
    }

    suspend fun getDog(id: String): NetworkResult<DogResponse> {
        return safeApiCall { api.getDog(id) }
    }

    suspend fun updateDog(id: String, request: UpdateDogRequest): NetworkResult<DogResponse> {
        val result = safeApiCall { api.updateDog(id, request) }

        if (result is NetworkResult.Success) {
            dogDao.insertDog(result.data.toEntity())
        }

        return result
    }

    suspend fun deleteDog(id: String): NetworkResult<Unit> {
        val result = safeApiCall { api.deleteDog(id) }

        if (result is NetworkResult.Success) {
            val entity = dogDao.getDogById(id)

            if (entity != null) dogDao.deleteDog(entity)
        }

        return result
    }

    private fun DogResponse.toEntity() = Dog(
        id = id,
        name = name,
        breed = breed,
        age = age,
        weight = weight,
        activityLevel = activityLevel,
        allergies = allergies,
        healthConditions = healthConditions,
        photoUrl = photoUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
