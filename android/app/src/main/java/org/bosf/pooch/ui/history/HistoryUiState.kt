package org.bosf.pooch.ui.history

import org.bosf.pooch.data.local.entities.ScanHistory

data class HistoryUiState(
    val items: List<ScanHistory> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)