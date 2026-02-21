package org.bosf.pooch.ui.home

import org.bosf.pooch.data.local.entities.Dog

data class HomeUiState(
    val dogs: List<Dog> = emptyList(),
    val selectedDog: Dog? = null,
    val userName: String = "",
    val isLoading: Boolean = true
)