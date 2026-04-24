package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salguero.importadoragtcliente.data.repository.VehiculoRepositoryImpl
import com.salguero.importadoragtcliente.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetalleViewModel : ViewModel() {
    private val repository = VehiculoRepositoryImpl()

    // Variable que guardará el carro cuando lo encontremos
    private val _vehiculo = MutableStateFlow<Vehiculo?>(null)
    val vehiculo: StateFlow<Vehiculo?> = _vehiculo

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    // Esta función la llamaremos desde la pantalla pasándole el ID
    fun cargarVehiculo(id: String) {
        viewModelScope.launch {
            _cargando.value = true
            val resultado = repository.obtenerVehiculoPorId(id)
            _vehiculo.value = resultado
            _cargando.value = false
        }
    }
}