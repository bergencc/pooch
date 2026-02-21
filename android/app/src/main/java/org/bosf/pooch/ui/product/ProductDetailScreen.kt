package org.bosf.pooch.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.bosf.pooch.ui.common.EcoScoreBadge
import org.bosf.pooch.ui.common.LoadingScreen
import org.bosf.pooch.ui.common.PoochTopBar
import org.bosf.pooch.ui.common.ProductThumbnail
import org.bosf.pooch.ui.common.SectionCard
import org.bosf.pooch.ui.scanner.ScanState
import org.bosf.pooch.ui.scanner.ScanViewModel

// ProductDetailScreen reads the most recent scan from the ScanViewModel
// and displays the result with recommendation
@Composable
fun ProductDetailScreen(
    scanId: String,
    onBack: () -> Unit,
    scanViewModel: ScanViewModel = hiltViewModel()
) {
    val scanState by scanViewModel.scanState.collectAsState()
    val scan = (scanState as? ScanState.Success)?.scan

    Scaffold(
        topBar = {
            PoochTopBar(
                title = "Scan Result",
                onBack = {
                    scanViewModel.reset()
                    onBack()
                }
            )
        }
    ) { padding ->
        if (scan == null) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Product header card
                Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        ProductThumbnail(url = scan.product.photoUrl, size = 72)

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(scan.product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                            scan.product.brand?.let {
                                Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                            }

                            Spacer(Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        scan.product.productType.replaceFirstChar { it.uppercase() },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }

                                EcoScoreBadge(scan.product.ecoScore)
                            }
                        }
                    }
                }

                // Recommendation card
                RecommendationCard(recommendation = scan.recommendation)

                // Ingredients
                if (scan.product.ingredients.isNotEmpty()) {
                    SectionCard(title = "Ingredients (${scan.product.ingredients.size})") {
                        scan.product.ingredients.chunked(4).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                row.forEach { ingredient ->
                                    val isDangerous = DANGEROUS_INGREDIENTS.any { ingredient.contains(it, ignoreCase = true) }

                                    Surface(
                                        color = if (isDangerous) MaterialTheme.colorScheme.errorContainer
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            ingredient,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isDangerous) MaterialTheme.colorScheme.error
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Nutrition
                if (scan.product.nutritionInfo?.isNotEmpty() == true) {
                    SectionCard(title = "Nutrition Facts") {
                        scan.product.nutritionInfo.entries.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(key, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }

                            if (key != scan.product.nutritionInfo.keys.last()) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 1.dp), thickness = 0.5.dp)
                            }
                        }
                    }
                }

                // Barcode info
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.QrCode, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(Modifier.width(8.dp))

                        Text("Barcode: ${scan.product.barcode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: String) {
    val isWarning = recommendation.contains("warning", ignoreCase = true)
            || recommendation.contains("danger", ignoreCase = true)
            || recommendation.contains("avoid", ignoreCase = true)
            || recommendation.contains("harmful", ignoreCase = true)
            || recommendation.contains("toxic", ignoreCase = true)

    val isGood = recommendation.contains("safe", ignoreCase = true)
            && !isWarning

    val (bgColor, iconColor, icon) = when {
        isWarning -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            Icons.Filled.Warning
        )
        isGood -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondary,
            Icons.Filled.CheckCircle
        )
        else -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            Icons.Filled.Info
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))

                Spacer(Modifier.width(8.dp))

                Text(
                    "Recommendation",
                    fontWeight = FontWeight.Bold,
                    color = iconColor,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                recommendation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private val DANGEROUS_INGREDIENTS = listOf(
    "xylitol", "chocolate", "cocoa", "grape", "raisin", "garlic", "onion",
    "macadamia", "caffeine", "avocado", "nutmeg", "alcohol"
)
