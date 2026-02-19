package org.bosf.pooch.data.repository

import kotlinx.coroutines.flow.Flow
import org.bosf.pooch.data.api.ApiService
import org.bosf.pooch.data.api.models.products.ProductResponse
import org.bosf.pooch.data.api.models.scans.ScanRequest
import org.bosf.pooch.data.api.models.scans.ScanResponse
import org.bosf.pooch.data.local.dao.ProductDao
import org.bosf.pooch.data.local.dao.ScanHistoryDao
import org.bosf.pooch.data.local.entities.Product
import org.bosf.pooch.data.local.entities.ScanHistory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRepository @Inject constructor(
    private val api: ApiService,
    private val productDao: ProductDao,
    private val scanHistoryDao: ScanHistoryDao
) {
    fun getScanHistoryForDog(dogId: String): Flow<List<ScanHistory>> =
        scanHistoryDao.getScanHistoryForDog(dogId)

    suspend fun submitScan(barcode: String, dogId: String): NetworkResult<ScanResponse> {
        val result = safeApiCall { api.submitScan(ScanRequest(barcode, dogId)) }

        if (result is NetworkResult.Success) {
            // Cache product
            productDao.insertProduct(result.data.product.toEntity())

            // Cache scan in history
            scanHistoryDao.insertScan(result.data.toHistoryEntity())
        }

        return result
    }

    suspend fun getProductByBarcode(barcode: String): NetworkResult<ProductResponse> {
        // Check cache first
        val cached = productDao.getProductByBarcode(barcode)
        val cacheAgeMs = System.currentTimeMillis() - (cached?.cachedAt ?: 0)

        if (cached != null && cacheAgeMs < CACHE_TTL_MS) {
            return NetworkResult.Success(cached.toResponse())
        }

        val result = safeApiCall { api.getProductByBarcode(barcode) }

        if (result is NetworkResult.Success) {
            productDao.insertProduct(result.data.toEntity())
        }

        return result
    }

    suspend fun refreshScanHistory(dogId: String): NetworkResult<Unit> {
        val result = safeApiCall { api.getScanHistory(dogId) }

        if (result is NetworkResult.Success) {
            val entities = result.data.items.map { it.toHistoryEntity() }

            scanHistoryDao.clearHistoryForDog(dogId)
            scanHistoryDao.insertScans(entities)

            // Also cache products
            result.data.items.forEach { scan ->
                productDao.insertProduct(scan.product.toEntity())
            }
        }

        return if (result is NetworkResult.Success) NetworkResult.Success(Unit)
        else result as NetworkResult<Unit>
    }

    private fun ProductResponse.toEntity() = Product(
        id = id,
        barcode = barcode,
        name = name,
        brand = brand,
        productType = productType,
        ingredients = ingredients,
        nutritionInfo = nutritionInfo,
        ecoScore = ecoScore,
        photoUrl = photoUrl,
        createdAt = createdAt
    )

    private fun Product.toResponse() = ProductResponse(
        id = id,
        barcode = barcode,
        name = name,
        brand = brand,
        productType = productType,
        ingredients = ingredients,
        nutritionInfo = nutritionInfo,
        ecoScore = ecoScore,
        photoUrl = photoUrl,
        createdAt = createdAt
    )

    private fun ScanResponse.toHistoryEntity() = ScanHistory(
        id = id,
        dogId = dogId,
        productId = product.id,
        productName = product.name,
        productBrand = product.brand,
        productPhotoUrl = product.photoUrl,
        recommendation = recommendation,
        createdAt = createdAt
    )

    companion object {
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L // 24 hours
    }
}
