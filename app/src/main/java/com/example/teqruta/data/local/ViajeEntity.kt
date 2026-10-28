package com.example.teqruta.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viajes")
data class ViajeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: String,
    val odometro: Double,
    val latitud: Double,
    val longitud: Double,
    val fotoRuta: String
)