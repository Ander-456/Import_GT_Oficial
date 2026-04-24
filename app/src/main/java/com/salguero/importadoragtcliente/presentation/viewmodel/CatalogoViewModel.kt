package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salguero.importadoragtcliente.data.repository.VehiculoRepositoryImpl
import com.salguero.importadoragtcliente.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogoViewModel : ViewModel() {
    private val repository = VehiculoRepositoryImpl()

    // Copia local de los vehiculos para no saturar Firebase con lecturas repetidas
    private var vehiculosMaestros = emptyList<Vehiculo>()

    // Lista filtrada que enviamos a la UI
    private val _vehiculos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculos: StateFlow<List<Vehiculo>> = _vehiculos

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    // Estados para los filtros de la pantalla
    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda

    private val _filtroSeleccionado = MutableStateFlow("Todos")
    val filtroSeleccionado: StateFlow<String> = _filtroSeleccionado

    init {
        cargarVehiculos()
    }

    private fun cargarVehiculos() {
        viewModelScope.launch {
            _cargando.value = true
            // Descarga inicial de la coleccion
            vehiculosMaestros = repository.obtenerVehiculos()
            aplicarFiltros()
            _cargando.value = false
        }
    }

    // Eventos disparados desde la UI
    fun onBuscarTextoCambiado(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
        aplicarFiltros()
    }

    fun onFiltroSeleccionado(nuevoFiltro: String) {
        _filtroSeleccionado.value = nuevoFiltro
        aplicarFiltros()
    }

    // Logica central de filtrado sobre la lista en memoria
    private fun aplicarFiltros() {
        var listaFiltrada = vehiculosMaestros

        // 1. Filtro por categoria exacta
        if (_filtroSeleccionado.value != "Todos") {
            listaFiltrada = listaFiltrada.filter {
                it.tipo.equals(_filtroSeleccionado.value, ignoreCase = true)
            }
        }

        // 2. Filtro de busqueda por coincidencia en marca, modelo o año
        if (_textoBusqueda.value.isNotBlank()) {
            listaFiltrada = listaFiltrada.filter {
                it.marca.contains(_textoBusqueda.value, ignoreCase = true) ||
                        it.modelo.contains(_textoBusqueda.value, ignoreCase = true) ||
                        it.anio.toString().contains(_textoBusqueda.value)
            }
        }

        // Actualizamos el flujo para la vista
        _vehiculos.value = listaFiltrada
    }
}