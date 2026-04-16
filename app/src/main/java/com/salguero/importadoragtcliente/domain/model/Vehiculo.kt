package com.salguero.importadoragtcliente.domain.model

data class Vehiculo(
    val id: String = "",
    val marca: String = "",
    val modelo: String = "",
    val anio: Int = 0,
    val precio: Double = 0.0,
    val tipo: String = "",
    val especificaciones: String = "",
    val imagenes: List<String> = emptyList(),
    val logoUrl: String = ""
)