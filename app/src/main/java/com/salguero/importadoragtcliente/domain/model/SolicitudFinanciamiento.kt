package com.salguero.importadoragtcliente.domain.model

data class SolicitudFinanciamiento(
    val id: String = "",
    val usuarioId: String = "",
    val nombreUsuario: String = "",
    val apellidoUsuario: String = "",
    val tipoPlan: String = "",
    val valorVehiculo: Double = 0.0,
    val enganche: Double = 0.0,
    val plazoMeses: Int = 0,
    val cuotaMensual: Double = 0.0,
    val estado: String = "en_revision", // "en_revision", "aceptada", "denegada"
    val fechaSolicitud: Long = System.currentTimeMillis()
)