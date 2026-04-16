package com.salguero.importadoragtcliente.domain.model

import com.google.firebase.firestore.DocumentId

data class Cita(
    @DocumentId
    val id: String = "",
    val vehiculoId: String = "",
    val marcaModelo: String = "",
    val clienteEmail: String = "",
    val fecha: String = "",
    val estado: String = "pendiente",
    val timestamp: Long = 0L
)