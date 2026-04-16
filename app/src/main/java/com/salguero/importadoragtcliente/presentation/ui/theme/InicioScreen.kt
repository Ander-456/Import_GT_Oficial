package com.salguero.importadoragtcliente.presentation.ui.theme

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.salguero.importadoragtcliente.presentation.viewmodel.InicioViewModel
import com.salguero.importadoragtcliente.domain.model.Vehiculo
import java.util.Locale

@Composable
fun InicioScreen(
    onNavigateToCatalogo: () -> Unit,
    onNavigateToCitas: () -> Unit,
    onNavigateToFinanciamiento: () -> Unit,
    onNavigateToVehiculoDetalle: (String) -> Unit,
    inicioViewModel: InicioViewModel = viewModel()
) {
    val banners by inicioViewModel.banners.collectAsState()
    val sugerenciasReales by inicioViewModel.sugerenciasAleatorias.collectAsState()
    val cargando by inicioViewModel.cargando.collectAsState()

    val formatearPrecio: (Double) -> String = { precio ->
        String.format(Locale.US, "%,.2f", precio)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- 1. BUSCADOR INTELIGENTE CON SOMBRA ---
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable { onNavigateToCatalogo() },
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface, // Actualizado para usar superficie
            shadowElevation = 4.dp // ¡Efecto flotante añadido!
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Buscar Vehículo...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (banners.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { banners.size })

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ) { page ->
                    val bannerActual = banners[page]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable {
                                if (bannerActual.vehiculoId.isNotEmpty()) {
                                    onNavigateToVehiculoDetalle(bannerActual.vehiculoId)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = bannerActual.imagenUrl,
                                contentDescription = "Banner Promocional",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(16.dp))
                            )

                            Surface(
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "¡Ver Oferta!",
                                    color = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (banners.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    IndicadorDePaginas(
                        totalPaginas = banners.size,
                        paginaActual = pagerState.currentPage
                    )
                }
            }
        } else if (cargando) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            IconoAcceso(Icons.Default.List, "Catálogo", onNavigateToCatalogo)
            IconoAcceso(Icons.Default.DateRange, "Citas", onNavigateToCitas)
            IconoAcceso(Icons.Default.Star, "Finanzas", onNavigateToFinanciamiento)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Sugerencias", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        if (sugerenciasReales.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 4000.dp),
                userScrollEnabled = false
            ) {
                items(sugerenciasReales) { vehiculo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToVehiculoDetalle(vehiculo.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        // --- 2. SUGERENCIAS MÁS FLOTANTES ---
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Actualizado a 6.dp
                    ) {
                        Column {
                            if (vehiculo.imagenes.isNotEmpty()) {
                                AsyncImage(
                                    model = vehiculo.imagenes[0],
                                    contentDescription = "Foto de ${vehiculo.marca}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)))
                            }

                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${vehiculo.marca} ${vehiculo.modelo}",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Año: ${vehiculo.anio}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Q${formatearPrecio(vehiculo.precio)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        } else if (!cargando) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                Text("No hay sugerencias disponibles.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// --- 3. BOTONES REDONDOS CON ÍCONOS BLANCO/NEGRO ---
@Composable
fun IconoAcceso(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(
            modifier = Modifier.size(65.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = CircleShape,
            shadowElevation = 6.dp
        ) {
            // EL CAMBIO ESTÁ AQUÍ EN EL TINT: Usamos onSurface para que sea Negro de día y Blanco de noche
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.padding(18.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 8.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun IndicadorDePaginas(totalPaginas: Int, paginaActual: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(totalPaginas) { iteracion ->
            val color = if (paginaActual == iteracion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            val ancho = if (paginaActual == iteracion) 16.dp else 8.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .height(8.dp)
                    .width(ancho)
            )
        }
    }
}