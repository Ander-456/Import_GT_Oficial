package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _exito = MutableStateFlow(false)
    val exito: StateFlow<Boolean> = _exito

    fun iniciarSesion(email: String, contrasena: String) {
        if (email.isBlank() || contrasena.isBlank()) {
            _error.value = "Por favor, ingresa tu correo y contraseña"
            return
        }
        _cargando.value = true
        _error.value = null
        auth.signInWithEmailAndPassword(email.trim(), contrasena.trim())
            .addOnCompleteListener { tarea ->
                _cargando.value = false
                if (tarea.isSuccessful) {
                    _exito.value = true
                } else {
                    _error.value = "Correo o contraseña incorrectos"
                }
            }
    }
}