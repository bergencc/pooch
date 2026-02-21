package org.bosf.pooch.ui.product

import org.bosf.pooch.data.local.entities.ScanHistory

data class ProductDetailUiState(
    val scan: ScanHistory? = null,
    val isLoading: Boolean = true
)