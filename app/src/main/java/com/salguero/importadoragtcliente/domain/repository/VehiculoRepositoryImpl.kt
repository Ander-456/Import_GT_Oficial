package com.salguero.importadoragtcliente.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.salguero.importadoragtcliente.domain.model.Vehiculo
import com.salguero.importadoragtcliente.domain.repository.VehiculoRepository
import kotlinx.coroutines.tasks.await

class VehiculoRepositoryImpl : VehiculoRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun obtenerVehiculos(): List<Vehiculo> {
        return try {
            val result = db.collection("Vehiculos").get().await()

            // Mapeo manual (A prueba de balas): Convertimos cada documento y le pegamos su ID real
            result.documents.mapNotNull { documento ->
                val vehiculo = documento.toObject(Vehiculo::class.java)
                vehiculo?.copy(id = documento.id)
            }
        } catch (e: Exception) {
            // Imprimimos el error real en la consola por si acaso
            Log.e("Repository", "Error descargando vehículos: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun obtenerVehiculoPorId(id: String): Vehiculo? {
        return try {
            val documento = db.collection("Vehiculos").document(id).get().await()
            val vehiculo = documento.toObject(Vehiculo::class.java)
            // Aseguramos que el carro individual también lleve su ID
            vehiculo?.copy(id = documento.id)
        } catch (e: Exception) {
            Log.e("Repository", "Error buscando el vehículo $id: ${e.message}")
            null
        }
    }
}