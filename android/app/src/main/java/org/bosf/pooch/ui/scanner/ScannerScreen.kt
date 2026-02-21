package org.bosf.pooch.ui.scanner

import android.Manifest.permission.CAMERA
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onScanSuccess: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val scanState by viewModel.scanState.collectAsState()
    val cameraPermission = rememberPermissionState(CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(scanState) {
        if (scanState is ScanState.Success) {
            onScanSuccess((scanState as ScanState.Success).scan.id)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (cameraPermission.status.isGranted) {
            CameraPreview(
                onBarcodeDetected = { viewModel.onBarcodeDetected(it) },
                isEnabled = scanState is ScanState.Idle || scanState is ScanState.Scanning
            )
        }

        // Top overlay
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(50))
            ) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }

            Spacer(Modifier.width(12.dp))

            Text(
                "Scan Barcode",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Scan frame guide
        if (scanState is ScanState.Idle || scanState is ScanState.Scanning) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.Center)
                    .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            )

            Text(
                "Point camera at a product barcode",
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 150.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // State overlays
        when (val state = scanState) {
            is ScanState.Processing -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(shape = RoundedCornerShape(16.dp)) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)

                            Spacer(Modifier.height(12.dp))

                            Text("Looking up product...", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            is ScanState.Error -> {
                ScanResultOverlay(
                    icon = Icons.Filled.ErrorOutline,
                    iconColor = MaterialTheme.colorScheme.error,
                    title = "Scan Failed",
                    message = state.message,
                    onDismiss = { viewModel.reset() }
                )
            }

            is ScanState.ProductNotFound -> {
                ScanResultOverlay(
                    icon = Icons.Filled.SearchOff,
                    iconColor = MaterialTheme.colorScheme.secondary,
                    title = "Product Not Found",
                    message = "Barcode ${state.barcode} is not in our database yet.",
                    onDismiss = { viewModel.reset() }
                )
            }

            is ScanState.Success -> {
                // Navigation handled via LaunchedEffect
            }

            else -> {}
        }

        if (!cameraPermission.status.isGranted) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.CameraAlt, null, tint = Color.White, modifier = Modifier.size(64.dp))

                Spacer(Modifier.height(16.dp))

                Text("Camera Permission Required", color = Color.White, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(8.dp))

                Text(
                    "PoochScan needs camera access to scan barcodes.",
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraPreview(
    onBarcodeDetected: (String) -> Unit,
    isEnabled: Boolean
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).also { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                if (!isEnabled) {
                                    imageProxy.close()
                                    return@setAnalyzer
                                }

                                val mediaImage = imageProxy.image

                                if (mediaImage != null) {
                                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                                    barcodeScanner.process(image)
                                        .addOnSuccessListener { barcodes ->
                                            barcodes.firstOrNull { it.format != Barcode.FORMAT_UNKNOWN }
                                                ?.rawValue
                                                ?.let(onBarcodeDetected)
                                        }
                                        .addOnFailureListener { Log.e("Scanner", "Barcode error", it) }
                                        .addOnCompleteListener { imageProxy.close() }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    try {
                        cameraProvider.unbindAll()

                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("Scanner", "Camera bind failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ScanResultOverlay(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(48.dp))

                Spacer(Modifier.height(12.dp))

                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                Text(message, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(20.dp))

                Button(onClick = onDismiss) { Text("Scan Again") }
            }
        }
    }
}
