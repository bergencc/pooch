package org.bosf.pooch.ui.dogs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.bosf.pooch.data.api.models.dogs.CreateDogRequest
import org.bosf.pooch.data.api.models.dogs.UpdateDogRequest
import org.bosf.pooch.data.repository.DogRepository
import org.bosf.pooch.data.repository.NetworkResult
import javax.inject.Inject

@HiltViewModel
class DogsViewModel @Inject constructor(
    private val dogRepository: DogRepository
) : ViewModel() {

    private val _dogsState = MutableStateFlow(DogsUiState())
    val dogsState: StateFlow<DogsUiState> = _dogsState.asStateFlow()

    private val _formState = MutableStateFlow(DogFormState())
    val formState: StateFlow<DogFormState> = _formState.asStateFlow()

    init {
        viewModelScope.launch {
            dogRepository.getAllDogs().collect { dogs ->
                _dogsState.value = DogsUiState(dogs = dogs, isLoading = false)
            }
        }

        refreshDogs()
    }

    fun refreshDogs() {
        viewModelScope.launch {
            _dogsState.value = _dogsState.value.copy(isLoading = true)
            val result = dogRepository.refreshDogs()

            if (result is NetworkResult.Error) {
                _dogsState.value = _dogsState.value.copy(error = result.message, isLoading = false)
            }
        }
    }

    fun loadDogForEdit(dogId: String) {
        viewModelScope.launch {
            _formState.value = DogFormState(isLoading = true)

            when (val result = dogRepository.getDog(dogId)) {
                is NetworkResult.Success -> _formState.value = DogFormState(dog = result.data)
                is NetworkResult.Error   -> _formState.value = DogFormState(error = result.message)
                else -> {}
            }
        }
    }

    fun createDog(
        name: String, breed: String, age: String, weight: String,
        activityLevel: String, allergies: List<String>, healthConditions: List<String>
    ) {
        if (name.isBlank()) {
            _formState.value = _formState.value.copy(error = "Dog name is required")

            return
        }

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSaving = true, error = null)

            val request = CreateDogRequest(
                name = name.trim(),
                breed = breed.takeIf { it.isNotBlank() },
                age = age.toIntOrNull(),
                weight = weight.toDoubleOrNull(),
                activityLevel = activityLevel.takeIf { it.isNotBlank() },
                allergies = allergies,
                healthConditions = healthConditions
            )

            when (val result = dogRepository.createDog(request)) {
                is NetworkResult.Success -> _formState.value = DogFormState(isSaved = true)
                is NetworkResult.Error   -> _formState.value = DogFormState(error = result.message)
                else -> {}
            }
        }
    }

    fun updateDog(
        dogId: String, name: String, breed: String, age: String, weight: String,
        activityLevel: String, allergies: List<String>, healthConditions: List<String>
    ) {
        if (name.isBlank()) {
            _formState.value = _formState.value.copy(error = "Dog name is required")

            return
        }

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSaving = true, error = null)

            val request = UpdateDogRequest(
                name = name.trim(),
                breed = breed.takeIf { it.isNotBlank() },
                age = age.toIntOrNull(),
                weight = weight.toDoubleOrNull(),
                activityLevel = activityLevel.takeIf { it.isNotBlank() },
                allergies = allergies,
                healthConditions = healthConditions
            )

            when (val result = dogRepository.updateDog(dogId, request)) {
                is NetworkResult.Success -> _formState.value = DogFormState(isSaved = true)
                is NetworkResult.Error   -> _formState.value = DogFormState(error = result.message)
                else -> {}
            }
        }
    }

    fun deleteDog(dogId: String) {
        viewModelScope.launch {
            dogRepository.deleteDog(dogId)
        }
    }

    fun clearFormError() {
        _formState.value = _formState.value.copy(error = null)
    }
}
