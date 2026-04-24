package com.salguero.importadoragtcliente.presentation.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.salguero.importadoragtcliente.presentation.viewmodel.UbicacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionScreen(
    onBackClick: () -> Unit,
    ubicacionViewModel: UbicacionViewModel = viewModel()
) {
    val ubicacion by ubicacionViewModel.coordenadasImportadora.collectAsState()
    val titulo by ubicacionViewModel.tituloMarcador.collectAsState()
    val descripcion by ubicacionViewModel.descripcionMarcador.collectAsState()

    // Dejamos que el estado inicie por defecto
    val cameraPositionState = rememberCameraPositionState()

    // Este efecto se lanza en cuanto la pantalla se dibuja y tenemos la ubicación
    LaunchedEffect(ubicacion) {
        // Hacemos una animación de cámara "volando" hacia Gualán (dura 2 segundos)
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(ubicacion, 16f),
            durationMs = 2000
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestra Ubicación", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState,
            // Agregamos padding para que los botones de zoom del mapa y el logo de Google
            // no queden debajo de tu barra de navegación flotante
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // El marcador aparecerá justo en Importadora GT
            Marker(
                state = MarkerState(position = ubicacion),
                title = titulo,
                snippet = descripcion
            )
        }
    }
}