package org.bosf.pooch.ui.dogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import org.bosf.pooch.ui.common.PoochTopBar

private val ACTIVITY_LEVELS = listOf("low", "medium", "high")
private val COMMON_ALLERGIES = listOf("chicken", "beef", "dairy", "wheat", "soy", "corn", "eggs", "fish")
private val COMMON_CONDITIONS = listOf("diabetes", "kidney disease", "heart disease", "obesity", "pancreatitis")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DogFormScreen(
    dogId: String?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: DogsViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    val isEditMode = dogId != null

    // Form fields
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf(listOf<String>()) }
    var healthConditions by remember { mutableStateOf(listOf<String>()) }
    var allergyInput by remember { mutableStateOf("") }
    var conditionInput by remember { mutableStateOf("") }

    // Load existing dog for editing
    LaunchedEffect(dogId) {
        if (dogId != null) {
            viewModel.loadDogForEdit(dogId)
        }
    }

    // Populate fields when dog loads
    LaunchedEffect(formState.dog) {
        formState.dog?.let { dog ->
            name = dog.name
            breed = dog.breed ?: ""
            age = dog.age?.toString() ?: ""
            weight = dog.weight?.toString() ?: ""
            activityLevel = dog.activityLevel ?: ""
            allergies = dog.allergies
            healthConditions = dog.healthConditions
        }
    }

    LaunchedEffect(formState.isSaved) {
        if (formState.isSaved) onSaved()
    }

    Scaffold(
        topBar = {
            PoochTopBar(
                title = if (isEditMode) "Edit Dog" else "Add Dog",
                onBack = onBack
            )
        }
    ) { padding ->
        if (formState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                formState.error?.let { error ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error)

                            Spacer(Modifier.width(8.dp))

                            Text(error, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                // Basic Info
                Text("Basic Info", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; viewModel.clearFormError() },
                    label = { Text("Dog Name *") },
                    leadingIcon = { Icon(Icons.Filled.Pets, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age (years)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (lbs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Activity Level
                Text("Activity Level", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ACTIVITY_LEVELS.forEach { level ->
                        FilterChip(
                            selected = activityLevel == level,
                            onClick = { activityLevel = if (activityLevel == level) "" else level },
                            label = { Text(level.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                // Allergies
                Text("Allergies", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = allergyInput,
                        onValueChange = { allergyInput = it },
                        label = { Text("Add allergy") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            val a = allergyInput.trim().lowercase()
                            if (a.isNotEmpty() && !allergies.contains(a)) {
                                allergies = allergies + a
                                allergyInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Add, "Add", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                // Common allergy chips
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    COMMON_ALLERGIES.forEach { a ->
                        FilterChip(
                            selected = allergies.contains(a),
                            onClick = {
                                allergies = if (allergies.contains(a)) allergies - a else allergies + a
                            },
                            label = { Text(a) }
                        )
                    }
                }

                // Custom allergies
                if (allergies.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        allergies.forEach { a ->
                            InputChip(
                                selected = true,
                                onClick = {},
                                label = { Text(a) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Filled.Close, "Remove",
                                        Modifier.size(16.dp).also { _ -> },
                                    )
                                }
                            )
                        }
                    }
                }

                // Health Conditions
                Text("Health Conditions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    COMMON_CONDITIONS.forEach { condition ->
                        FilterChip(
                            selected = healthConditions.contains(condition),
                            onClick = {
                                healthConditions = if (healthConditions.contains(condition))
                                    healthConditions - condition
                                else
                                    healthConditions + condition
                            },
                            label = { Text(condition.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (isEditMode && dogId != null) {
                            viewModel.updateDog(dogId, name, breed, age, weight, activityLevel, allergies, healthConditions)
                        } else {
                            viewModel.createDog(name, breed, age, weight, activityLevel, allergies, healthConditions)
                        }
                    },
                    enabled = !formState.isSaving,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    if (formState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Icon(if (isEditMode) Icons.Filled.Save else Icons.Filled.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (isEditMode) "Save Changes" else "Add Dog", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}