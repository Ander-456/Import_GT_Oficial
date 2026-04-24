package com.salguero.importadoragtcliente.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.salguero.importadoragtcliente.domain.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritosViewModel : ViewModel() {
    // Referencias a los servicios de Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Flujos de estado reactivos para la interfaz gráfica
    private val _vehiculosFavoritos = MutableStateFlow<List<Vehiculo>>(emptyList())
    val vehiculosFavoritos = _vehiculosFavoritos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _idsFavoritos = MutableStateFlow<List<String>>(emptyList())
    val idsFavoritos = _idsFavoritos.asStateFlow()

    init {
        // Iniciamos el listener en tiempo real al instanciar el ViewModel
        escucharFavoritos()
    }

    private fun escucharFavoritos() {
        val userEmail = auth.currentUser?.email ?: return
        _cargando.value = true

        // Escuchamos los cambios en tiempo real del documento del usuario
        db.collection("favoritos").document(userEmail)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _cargando.value = false
                    return@addSnapshotListener
                }

                // Extraemos el array de IDs
                val idsGuardados = snapshot?.get("vehiculosIds") as? List<String> ?: emptyList()
                _idsFavoritos.value = idsGuardados

                if (idsGuardados.isEmpty()) {
                    _vehiculosFavoritos.value = emptyList()
                    _cargando.value = false
                } else {
                    // Consulta general para mapear los IDs con los datos completos de los vehículos
                    db.collection("Vehiculos").get()
                        .addOnSuccessListener { vehiculosSnapshot ->
                            // Transformación de documentos a objetos de dominio asegurando la inyección del ID
                            val todosLosVehiculos = vehiculosSnapshot.documents.mapNotNull { documento ->
                                val vehiculo = documento.toObject(Vehiculo::class.java)
                                vehiculo?.copy(id = documento.id)
                            }

                            // Filtramos la lista completa basándonos en los IDs cacheados del usuario
                            _vehiculosFavoritos.value = todosLosVehiculos.filter { it.id in idsGuardados }
                            _cargando.value = false
                        }
                }
            }
    }

    // Lógica transaccional para agregar o remover un vehículo de favoritos
    fun toggleFavorito(vehiculoId: String) {
        val userEmail = auth.currentUser?.email ?: return
        val docRef = db.collection("favoritos").document(userEmail)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val idsActuales = snapshot.get("vehiculosIds") as? MutableList<String> ?: mutableListOf()

            // Verificamos existencia para decidir si hacemos push o pull del ID
            if (idsActuales.contains(vehiculoId)) {
                idsActuales.remove(vehiculoId)
            } else {
                idsActuales.add(vehiculoId)
            }

            transaction.set(docRef, mapOf("vehiculosIds" to idsActuales))
        }
    }
}