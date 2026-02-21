package org.bosf.pooch.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.bosf.pooch.data.repository.NetworkResult
import org.bosf.pooch.data.repository.ScanRepository
import javax.inject.Inject

@HiltViewModel
class ScanHistoryViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory(dogId: String) {
        viewModelScope.launch {
            scanRepository.getScanHistoryForDog(dogId).collect { items ->
                _uiState.value = _uiState.value.copy(items = items, isLoading = false)
            }
        }

        refresh(dogId)
    }

    fun refresh(dogId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            val result = scanRepository.refreshScanHistory(dogId)

            if (result is NetworkResult.Error) {
                _uiState.value = _uiState.value.copy(error = result.message)
            }

            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }
}