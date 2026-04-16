package com.salguero.importadoragtcliente.domain.model

data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "cliente",
    val favoritos: List<String> = emptyList() // Aquí guardaremos los IDs de los carros favoritos
)
