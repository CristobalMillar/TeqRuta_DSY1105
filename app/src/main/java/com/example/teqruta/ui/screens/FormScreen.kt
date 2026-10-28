package com.example.teqruta.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.teqruta.ui.viewmodel.ViajeViewModel

@Composable
fun FormScreen(
    viewModel: ViajeViewModel,
    onNavigateToList: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    // Notificación Toast
    LaunchedEffect(state.isGuardadoExitoso) {
        if (state.isGuardadoExitoso) {
            Toast.makeText(context, "¡Viaje guardado exitosamente!", Toast.LENGTH_SHORT).show()
            viewModel.resetGuardado()
        }
    }

    // Lanzador para capturar la foto de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            viewModel.onPhotoCaptured("foto_${System.currentTimeMillis()}.jpg")
        }
    }

    // Lanzador para solicitar permiso de Cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // Lanzador para solicitar permiso de GPS
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.onLocationCaptured(-33.4489, -70.6693)
        } else {
            Toast.makeText(context, "Se requiere permiso de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registrar Traslado",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Ingrese los datos del vehículo para calcular costos",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo Odómetro con manejo de error
        OutlinedTextField(
            value = state.odometro,
            onValueChange = { viewModel.onOdometroChange(it) },
            label = { Text("Odómetro actual (km)") },
            isError = state.odometroError != null,
            supportingText = {
                state.odometroError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón Ubicación de GPS
        Button(
            onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    viewModel.onLocationCaptured(-33.4489, -70.6693)
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (state.latitud != 0.0) "Ubicación Capturada ✓" else "Capturar Ubicación GPS")
        }
        state.gpsError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Botón Cámara (pide permiso primero, abre la cámara después)
        Button(
            onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    cameraLauncher.launch(null)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (state.fotoRuta.isNotBlank()) "Foto Capturada ✓" else "Tomar Foto del Odómetro")
        }
        state.fotoError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Guardar
        Button(
            onClick = { viewModel.guardarViaje() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Viaje")
        }

        // Botón Ver Historial
        OutlinedButton(
            onClick = onNavigateToList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Historial de Viajes")
        }
    }
}