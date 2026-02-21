package org.bosf.pooch.ui.dogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.bosf.pooch.data.local.entities.Dog
import org.bosf.pooch.ui.common.DogAvatar
import org.bosf.pooch.ui.common.LoadingScreen
import org.bosf.pooch.ui.common.PoochTopBar

@Composable
fun DogsScreen(
    onNavigateToAddDog: () -> Unit,
    onNavigateToDogDetail: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: DogsViewModel = hiltViewModel()
) {
    val uiState by viewModel.dogsState.collectAsState()
    var deletingDog by remember { mutableStateOf<Dog?>(null) }

    deletingDog?.let { dog ->
        AlertDialog(
            onDismissRequest = { deletingDog = null },
            title = { Text("Remove ${dog.name}?") },
            text = { Text("This will delete ${dog.name}'s profile and scan history.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDog(dog.id)
                    deletingDog = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deletingDog = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            PoochTopBar(
                title = "My Dogs",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddDog,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, "Add dog", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.dogs.isEmpty() -> LoadingScreen()

            uiState.dogs.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Pets, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No dogs yet", style = MaterialTheme.typography.titleLarge)
                    Text("Add your first dog to get started", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onNavigateToAddDog) { Text("Add Dog") }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.dogs) { dog ->
                        DogListItem(
                            dog = dog,
                            onTap = { onNavigateToDogDetail(dog.id) },
                            onDelete = { deletingDog = dog }
                        )
                    }

                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DogListItem(
    dog: Dog,
    onTap: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onTap,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DogAvatar(photoUrl = dog.photoUrl, name = dog.name, size = 56)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(dog.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)

                val details = buildList {
                    dog.breed?.let { add(it) }
                    dog.age?.let { add("$it yr") }
                    dog.weight?.let { add("%.1f lbs".format(it)) }
                }.joinToString(" · ")

                if (details.isNotEmpty()) {
                    Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                if (dog.allergies.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))

                    Text(
                        "⚠ ${dog.allergies.size} allerg${if (dog.allergies.size == 1) "y" else "ies"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
            }

            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
