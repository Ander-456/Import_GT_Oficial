package com.salguero.importadoragtcliente.presentation.ui.theme

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.auth.FirebaseAuth
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

    // Extraccion del primer nombre del usuario autenticado
    val auth = FirebaseAuth.getInstance()
    val usuario = auth.currentUser
    val nombreParaMostrar = usuario?.displayName?.split(" ")?.get(0) ?: "Usuario"

    val formatearPrecio: (Double) -> String = { precio ->
        String.format(Locale.US, "%,.2f", precio)
    }

    // Se reemplaza el Column con verticalScroll por un LazyColumn para evitar conflictos de scroll
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Header
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Hola, $nombreParaMostrar! ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Descubre tu",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Próximo Vehículo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Barra de busqueda
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clickable { onNavigateToCatalogo() },
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Buscar modelo, marca, año...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Carrusel promocional
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
                        Spacer(modifier = Modifier.height(12.dp))
                        IndicadorDePaginas(totalPaginas = banners.size, paginaActual = pagerState.currentPage)
                    }
                }
            } else if (cargando) {
                Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accesos directos
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                IconoAcceso(Icons.Default.List, "Catálogo", onNavigateToCatalogo)
                IconoAcceso(Icons.Default.DateRange, "Citas", onNavigateToCitas)
                IconoAcceso(Icons.Default.Star, "Finanzas", onNavigateToFinanciamiento)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Titulo de sugerencias
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recomendados para ti", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = "Ver todos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToCatalogo() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Renderizado del Grid sin anidar scroll
        if (sugerenciasReales.isNotEmpty()) {
            // Dividimos la lista en bloques de 2 para simular la cuadricula
            val filas = sugerenciasReales.chunked(2)

            items(filas) { filaDeVehiculos ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (vehiculo in filaDeVehiculos) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onNavigateToVehiculoDetalle(vehiculo.id) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column {
                                Box {
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

                                    // Etiqueta del año
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Text(
                                            text = vehiculo.anio.toString(),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
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
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Q${formatearPrecio(vehiculo.precio)}",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                    // Si la fila tiene numero impar de elementos, se compensa el espacio
                    if (filaDeVehiculos.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else if (!cargando) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No hay sugerencias disponibles.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun IconoAcceso(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(
            modifier = Modifier.size(65.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = CircleShape,
            shadowElevation = 6.dp
        ) {
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