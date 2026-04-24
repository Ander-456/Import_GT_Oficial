package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class PerfilViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    // Obtenemos el correo del usuario que está conectado ahorita
    fun obtenerCorreoUsuario(): String {
        return auth.currentUser?.email ?: "Usuario Invitado"
    }

    // Función para matar la sesión
    fun cerrarSesion() {
        auth.signOut()
    }
}