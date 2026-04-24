package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UbicacionViewModel : ViewModel() {

    // Coordenadas exactas de la Plazuela Municipal en Gualán
    private val _coordenadasImportadora = MutableStateFlow(LatLng(15.113219, -89.355269))
    val coordenadasImportadora = _coordenadasImportadora.asStateFlow()

    // Textos del marcador en el mapa
    private val _tituloMarcador = MutableStateFlow("Importadora GT")
    val tituloMarcador = _tituloMarcador.asStateFlow()

    private val _descripcionMarcador = MutableStateFlow("Tu próximo vehículo te espera aquí")
    val descripcionMarcador = _descripcionMarcador.asStateFlow()
}