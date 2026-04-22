package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RegistroViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _exito = MutableStateFlow(false)
    val exito: StateFlow<Boolean> = _exito

    fun crearCuenta(email: String, contrasena: String, confirmarContrasena: String) {
        if (email.isBlank() || contrasena.isBlank() || confirmarContrasena.isBlank()) {
            _error.value = "Por favor, llena todos los campos"
            return
        }

        if (contrasena != confirmarContrasena) {
            _error.value = "Las contraseñas no coinciden"
            return
        }

        if (contrasena.length < 6) {
            _error.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        _cargando.value = true
        _error.value = null

        // CREAR usuario nuevo
        auth.createUserWithEmailAndPassword(email, contrasena)
            .addOnCompleteListener { tarea ->
                _cargando.value = false
                if (tarea.isSuccessful) {
                    _exito.value = true
                } else {
                    _error.value = "Hubo un error al crear la cuenta. Verifica tu correo."
                }
            }
    }
}