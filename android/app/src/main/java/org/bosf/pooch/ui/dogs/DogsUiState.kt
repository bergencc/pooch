package org.bosf.pooch.ui.dogs

import org.bosf.pooch.data.local.entities.Dog

data class DogsUiState(
    val dogs: List<Dog> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)