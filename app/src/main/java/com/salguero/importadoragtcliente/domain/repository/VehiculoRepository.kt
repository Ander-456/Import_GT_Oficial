package com.salguero.importadoragtcliente.domain.repository

import com.salguero.importadoragtcliente.domain.model.Vehiculo

interface VehiculoRepository {
    // Suspend significa que esta función toma tiempo (porque va a internet)
    // y no debe congelar la pantalla del usuario.
    suspend fun obtenerVehiculos(): List<Vehiculo>
    suspend fun obtenerVehiculoPorId(id: String): Vehiculo?
}