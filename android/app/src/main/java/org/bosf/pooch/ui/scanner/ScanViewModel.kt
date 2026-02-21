package org.bosf.pooch.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.bosf.pooch.data.local.TokenStore
import org.bosf.pooch.data.repository.NetworkResult
import org.bosf.pooch.data.repository.ScanRepository
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    val selectedDogId: Flow<String?> = tokenStore.selectedDogId

    private var lastScannedBarcode: String? = null

    fun onBarcodeDetected(barcode: String) {
        if (barcode == lastScannedBarcode || _scanState.value is ScanState.Processing) return

        lastScannedBarcode = barcode

        submitScan(barcode)
    }

    private fun submitScan(barcode: String) {
        viewModelScope.launch {
            val dogId = tokenStore.selectedDogId.first()

            if (dogId == null) {
                _scanState.value = ScanState.Error("No dog selected. Please select a dog on the home screen.")

                return@launch
            }

            _scanState.value = ScanState.Processing

            when (val result = scanRepository.submitScan(barcode, dogId)) {
                is NetworkResult.Success -> _scanState.value = ScanState.Success(result.data)
                is NetworkResult.Error -> {
                    if (result.code == 404) {
                        _scanState.value = ScanState.ProductNotFound(barcode)
                    } else {
                        _scanState.value = ScanState.Error(result.message)
                    }
                }
                else -> {}
            }
        }
    }

    fun reset() {
        lastScannedBarcode = null
        _scanState.value = ScanState.Idle
    }
}
