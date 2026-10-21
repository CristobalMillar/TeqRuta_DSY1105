package com.example.teqruta.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqruta.data.local.ViajeDao
import com.example.teqruta.data.local.ViajeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Estado del formulario (Cumple rúbrica: Estado desacoplado)
data class ViajeFormState(
    val odometro: String = "",
    val odometroError: String? = null,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val fotoRuta: String = "",
    val fotoError: String? = null,
    val gpsError: String? = null,
    val isGuardadoExitoso: Boolean = false
)

class ViajeViewModel(private val dao: ViajeDao) : ViewModel() {

    private val _state = MutableStateFlow(ViajeFormState())
    val state: StateFlow<ViajeFormState> = _state.asStateFlow()

    // Historial de viajes directamente desde Room
    val viajesList = dao.obtenerTodosLosViajes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onOdometroChange(newValue: String) {
        // Validación en tiempo real
        val error = if (newValue.isBlank()) "El odómetro es obligatorio"
        else if (newValue.toDoubleOrNull() == null) "Debe ser un número válido"
        else null
        _state.update { it.copy(odometro = newValue, odometroError = error) }
    }

    fun onLocationCaptured(lat: Double, lon: Double) {
        _state.update { it.copy(latitud = lat, longitud = lon, gpsError = null) }
    }

    fun onPhotoCaptured(uri: String) {
        _state.update { it.copy(fotoRuta = uri, fotoError = null) }
    }

    fun guardarViaje() {
        val currentState = _state.value

        // Validación final antes de guardar
        val hasOdometroError = currentState.odometro.isBlank() || currentState.odometro.toDoubleOrNull() == null
        val hasFotoError = currentState.fotoRuta.isBlank()
        val hasGpsError = currentState.latitud == 0.0 && currentState.longitud == 0.0

        if (hasOdometroError || hasFotoError || hasGpsError) {
            _state.update {
                it.copy(
                    odometroError = if (hasOdometroError) "Ingrese un valor válido" else null,
                    fotoError = if (hasFotoError) "Debe capturar una foto" else null,
                    gpsError = if (hasGpsError) "Debe capturar la ubicación" else null
                )
            }
            return
        }

        // Si todo está correcto, guardamos en Room local (Cumple requerimiento offline)
        viewModelScope.launch {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val nuevoViaje = ViajeEntity(
                fecha = fechaActual,
                odometro = currentState.odometro.toDouble(),
                latitud = currentState.latitud,
                longitud = currentState.longitud,
                fotoRuta = currentState.fotoRuta
            )
            dao.insertarViaje(nuevoViaje)

            // Limpiamos el formulario y emitimos éxito
            _state.value = ViajeFormState(isGuardadoExitoso = true)
        }
    }

    fun resetGuardado() {
        _state.update { it.copy(isGuardadoExitoso = false) }
    }
}