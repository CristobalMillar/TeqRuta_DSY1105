package com.example.teqruta.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ViajeDao {
    @Insert
    suspend fun insertarViaje(viaje: ViajeEntity)

    @Query("SELECT * FROM viajes ORDER BY id DESC")
    fun obtenerTodosLosViajes(): Flow<List<ViajeEntity>>
}