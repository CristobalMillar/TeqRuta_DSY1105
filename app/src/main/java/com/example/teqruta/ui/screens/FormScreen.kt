package com.example.teqruta.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.teqruta.ui.viewmodel.ViajeViewModel
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun FormScreen(viewModel: ViajeViewModel, onNavigateToList: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Cliente de Google Play Services para GPS
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // 1. Recurso Nativo: Lanzador de Permisos y Captura de GPS
    val gpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.onLocationCaptured(location.latitude, location.longitude)
                } else {
                    // Fallback: Si el emulador no tiene señal GPS, simulamos la de Concepción para no bloquearte
                    viewModel.onLocationCaptured(-36.8201, -73.0444)
                }
            }
        }
    }

    // 2. Recurso Nativo: Lanzador de Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Guardamos un nombre referencial simulando la ruta del archivo
            viewModel.onPhotoCaptured("foto_odometro_${System.currentTimeMillis()}.jpg")
        }
    }

    // Navegación automática al guardar exitosamente
    LaunchedEffect(state.isGuardadoExitoso) {
        if (state.isGuardadoExitoso) {
            viewModel.resetGuardado()
            onNavigateToList()
        }
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Registrar Traslado", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text("Ingrese los datos del vehículo para calcular costos", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Campo validado con icono
        OutlinedTextField(
            value = state.odometro,
            onValueChange = { viewModel.onOdometroChange(it) },
            label = { Text("Odómetro actual (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = state.odometroError != null,
            trailingIcon = {
                if (state.odometroError != null) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = Color.Red)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        // Animación funcional de error
        AnimatedVisibility(visible = state.odometroError != null) {
            Text(text = state.odometroError ?: "", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón GPS
        Button(onClick = { gpsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.LocationOn, contentDescription = "GPS")
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (state.latitud != 0.0) "Ubicación Capturada (${state.latitud}, ${state.longitud})" else "Capturar Ubicación GPS")
        }
        AnimatedVisibility(visible = state.gpsError != null) {
            Text(text = state.gpsError ?: "", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Cámara
        Button(onClick = { cameraLauncher.launch(null) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (state.fotoRuta.isNotEmpty()) "Foto Capturada" else "Tomar Foto del Odómetro")
        }
        AnimatedVisibility(visible = state.fotoError != null) {
            Text(text = state.fotoError ?: "", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.guardarViaje() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Guardar Viaje")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = { onNavigateToList() }, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Historial de Viajes")
        }
    }
}