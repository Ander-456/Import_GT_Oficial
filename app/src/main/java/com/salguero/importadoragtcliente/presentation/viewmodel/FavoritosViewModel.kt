package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoritosViewModel : ViewModel() {
    private val _idsFavoritos = MutableStateFlow<Set<String>>(emptySet())
    val idsFavoritos: StateFlow<Set<String>> = _idsFavoritos

    fun toggleFavorito(vehiculoId: String) {
        val actuales = _idsFavoritos.value.toMutableSet()
        if (actuales.contains(vehiculoId)) {
            actuales.remove(vehiculoId)
        } else {
            actuales.add(vehiculoId)
        }
        _idsFavoritos.value = actuales
    }
}