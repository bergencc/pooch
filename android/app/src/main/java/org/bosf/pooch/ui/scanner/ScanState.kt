package org.bosf.pooch.ui.scanner

import org.bosf.pooch.data.api.models.scans.ScanResponse

sealed class ScanState {
    data object Idle : ScanState()

    data object Scanning : ScanState()

    data object Processing : ScanState()

    data class Success(val scan: ScanResponse) : ScanState()

    data class Error(val message: String) : ScanState()

    data class ProductNotFound(val barcode: String) : ScanState()
}