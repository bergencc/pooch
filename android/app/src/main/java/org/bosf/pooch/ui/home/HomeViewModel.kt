package org.bosf.pooch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.bosf.pooch.data.local.TokenStore
import org.bosf.pooch.data.local.entities.Dog
import org.bosf.pooch.data.repository.AuthRepository
import org.bosf.pooch.data.repository.DogRepository
import org.bosf.pooch.data.repository.NetworkResult
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Load user
            val userResult = authRepository.getCurrentUser()
            val userName = if (userResult is NetworkResult.Success) userResult.data.name else ""

            // Observe local dogs
            dogRepository.getAllDogs()
                .combine(tokenStore.selectedDogId) { dogs, selectedId ->
                    val selected = dogs.find { it.id == selectedId } ?: dogs.firstOrNull()

                    Triple(dogs, selected, userName)
                }
                .collect { (dogs, selected, name) ->
                    _uiState.value = HomeUiState(
                        dogs = dogs,
                        selectedDog = selected,
                        userName = name,
                        isLoading = false
                    )
                }
        }

        // Refresh dogs from API
        viewModelScope.launch {
            dogRepository.refreshDogs()
        }
    }

    fun selectDog(dog: Dog) {
        viewModelScope.launch {
            tokenStore.saveSelectedDog(dog.id)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
