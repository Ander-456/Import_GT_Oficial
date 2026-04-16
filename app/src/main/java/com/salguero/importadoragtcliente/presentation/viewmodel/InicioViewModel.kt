package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.salguero.importadoragtcliente.domain.model.Vehiculo
import com.salguero.importadoragtcliente.domain.model.Banner // <-- IMPORTAMOS EL NUEVO

class InicioViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners

    private val _sugerenciasAleatorias = MutableStateFlow<List<Vehiculo>>(emptyList())
    val sugerenciasAleatorias: StateFlow<List<Vehiculo>> = _sugerenciasAleatorias

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        cargarDatosDeInicio()
    }

    private fun cargarDatosDeInicio() {
        _cargando.value = true
        // 1. Cargamos TODOS los banners
        db.collection("banners").get()
            .addOnSuccessListener { resultado ->
                if (!resultado.isEmpty) {
                    val listaBanners = mutableListOf<Banner>()
                    for (documento in resultado) {
                        try {
                            val banner = documento.toObject(Banner::class.java).copy(id = documento.id)
                            listaBanners.add(banner)
                        } catch (e: Exception) {
                            println("Error al convertir banner")
                        }
                    }
                    _banners.value = listaBanners
                }
                cargarVehiculosParaSugerencias()
            }
            .addOnFailureListener {
                cargarVehiculosParaSugerencias()
            }
    }

    private fun cargarVehiculosParaSugerencias() {
        db.collection("Vehiculos").get()
            .addOnSuccessListener { resultado ->
                if (!resultado.isEmpty) {
                    val todosLosVehiculos = mutableListOf<Vehiculo>()
                    for (documento in resultado) {
                        try {
                            val vehiculo = documento.toObject(Vehiculo::class.java)
                            val vehiculoConId = vehiculo.copy(id = documento.id)
                            todosLosVehiculos.add(vehiculoConId)
                        } catch (e: Exception) {
                            println("Error al convertir vehículo: ${e.message}")
                        }
                    }
                    if (todosLosVehiculos.isNotEmpty()) {
                        // Mezclamos todos y tomamos un máximo de 20 para hacer la cuadrícula
                        val sugerencias = todosLosVehiculos.shuffled().take(20)
                        _sugerenciasAleatorias.value = sugerencias
                    }
                }
                _cargando.value = false
            }
            .addOnFailureListener {
                _cargando.value = false
            }
    }
}