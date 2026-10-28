package com.example.teqruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.teqruta.ui.viewmodel.ViajeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(viewModel: ViajeViewModel, onNavigateBack: () -> Unit) {
    val viajes by viewModel.viajesList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Traslados") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viajes) { viaje ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fecha: ${viaje.fecha}", style = MaterialTheme.typography.titleMedium)
                        Text("Odómetro: ${viaje.odometro} km")
                        Text("Ubicación: ${viaje.latitud}, ${viaje.longitud}")
                    }
                }
            }
        }
    }
}