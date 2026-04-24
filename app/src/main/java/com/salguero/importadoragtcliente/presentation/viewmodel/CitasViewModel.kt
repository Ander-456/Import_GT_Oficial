package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel

class CitasViewModel : ViewModel() {
    // Función simulada para que el botón "Agendar" muestre el mensaje de éxito
    fun agendarCita(
        vehiculoId: String,
        marcaModelo: String,
        fecha: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Aquí en el futuro se conectará a Firebase para guardar la cita.
        // Por ahora, simulamos que todo salió perfecto para el avance.
        onSuccess()
    }
}