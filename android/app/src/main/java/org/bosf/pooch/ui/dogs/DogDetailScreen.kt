package org.bosf.pooch.ui.dogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.bosf.pooch.ui.common.DogAvatar
import org.bosf.pooch.ui.common.LoadingScreen
import org.bosf.pooch.ui.common.PoochTopBar
import org.bosf.pooch.ui.common.SectionCard

@Composable
fun DogDetailScreen(
    dogId: String,
    onNavigateToEdit: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBack: () -> Unit,
    viewModel: DogsViewModel = hiltViewModel()
) {
    val dogsState by viewModel.dogsState.collectAsState()
    val dog = dogsState.dogs.find { it.id == dogId }

    Scaffold(
        topBar = {
            PoochTopBar(
                title = dog?.name ?: "Dog Profile",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Filled.Edit, "Edit")
                    }
                }
            )
        }
    ) { padding ->
        if (dog == null) {
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
                // Header
                Card(shape = MaterialTheme.shapes.large) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        DogAvatar(photoUrl = dog.photoUrl, name = dog.name, size = 72)

                        Spacer(Modifier.width(16.dp))

                        Column {
                            Text(dog.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                            dog.breed?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 4.dp)) {
                                dog.age?.let { InfoPill("$it yr") }
                                dog.weight?.let { InfoPill("%.1f lbs".format(it)) }
                                dog.activityLevel?.let { InfoPill(it) }
                            }
                        }
                    }
                }

                // Allergies
                if (dog.allergies.isNotEmpty()) {
                    SectionCard(title = "Allergies") {
                        FlowRowChips(dog.allergies, chipColor = MaterialTheme.colorScheme.errorContainer, textColor = MaterialTheme.colorScheme.error)
                    }
                }

                // Health Conditions
                if (dog.healthConditions.isNotEmpty()) {
                    SectionCard(title = "Health Conditions") {
                        FlowRowChips(dog.healthConditions, chipColor = MaterialTheme.colorScheme.tertiaryContainer, textColor = MaterialTheme.colorScheme.tertiary)
                    }
                }

                // Actions
                Button(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.History, null)
                    Spacer(Modifier.width(8.dp))
                    Text("View Scan History")
                }
            }
        }
    }
}

@Composable
private fun InfoPill(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowChips(
    items: List<String>,
    chipColor: Color,
    textColor: Color
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { item ->
            Surface(color = chipColor, shape = MaterialTheme.shapes.small) {
                Text(
                    item.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}
