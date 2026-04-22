package com.salguero.importadoragtcliente.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.salguero.importadoragtcliente.presentation.viewmodel.CatalogoViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    maxPrecio: Float = -1f,
    onVehiculoClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToPerfil: () -> Unit = {},
    catalogoViewModel: CatalogoViewModel = viewModel()
) {
    val vehiculos by catalogoViewModel.vehiculos.collectAsState()
    val cargando by catalogoViewModel.cargando.collectAsState()
    val busqueda by catalogoViewModel.textoBusqueda.collectAsState()
    val filtroSeleccionado by catalogoViewModel.filtroSeleccionado.collectAsState()

    val categorias = listOf(
        "Todos", "sedan", "pick-up", "deportivo", "moto",
        "suv", "hatchback", "offroad", "premium",
        "muscle", "exotico", "modificados", "trabajo"
    )

    val formatearPrecio: (Double) -> String = { precio ->
        NumberFormat.getNumberInstance(Locale.US).format(precio)
    }

    val vehiculosFiltrados = vehiculos.filter { vehiculo ->
        if (maxPrecio > 0.0f) vehiculo.precio <= maxPrecio else true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titulo = if (maxPrecio > 0f) "Vehículos Aprobados" else "Importadora GT"
                    Text(titulo, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                },
                navigationIcon = {
                    if (maxPrecio > 0f) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {


            if (maxPrecio > 0f) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Mostrando vehículos hasta Q${formatearPrecio(maxPrecio.toDouble())}",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // BUSCADOR
            OutlinedTextField(
                value = busqueda,
                onValueChange = { catalogoViewModel.onBuscarTextoCambiado(it) },
                placeholder = { Text("Buscar vehículo...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // CATEGORÍAS
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categorias) { categoria ->
                    FilterChip(
                        selected = filtroSeleccionado == categoria,
                        onClick = { catalogoViewModel.onFiltroSeleccionado(categoria) },
                        label = { Text(categoria.replaceFirstChar { it.uppercase() }) },
                        shape = RoundedCornerShape(16.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (vehiculosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No se encontraron vehículos disponibles.",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(vehiculosFiltrados) { vehiculo ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onVehiculoClick(vehiculo.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column {
                                AsyncImage(
                                    model = vehiculo.imagenes.getOrNull(0) ?: "",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = "${vehiculo.marca} ${vehiculo.modelo}", fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Q${formatearPrecio(vehiculo.precio)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}