package org.bosf.pooch.ui.dogs

import org.bosf.pooch.data.api.models.dogs.DogResponse

data class DogFormState(
    val dog: DogResponse? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)