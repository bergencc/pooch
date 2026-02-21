package org.bosf.pooch.ui.product

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.bosf.pooch.data.local.dao.ScanHistoryDao
import javax.inject.Inject


@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val scanHistoryDao: ScanHistoryDao
) : ViewModel()
