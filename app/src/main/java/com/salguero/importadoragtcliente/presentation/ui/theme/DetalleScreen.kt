package com.salguero.importadoragtcliente.presentation.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.salguero.importadoragtcliente.presentation.viewmodel.CitasViewModel
import com.salguero.importadoragtcliente.presentation.viewmodel.DetalleViewModel
import com.salguero.importadoragtcliente.presentation.viewmodel.FavoritosViewModel
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    vehiculoId: String,
    onBackClick: () -> Unit,
    detalleViewModel: DetalleViewModel = viewModel(),
    citasViewModel: CitasViewModel = viewModel(),
    favoritosViewModel: FavoritosViewModel = viewModel()
) {
    LaunchedEffect(vehiculoId) { detalleViewModel.cargarVehiculo(vehiculoId) }

    val vehiculoState by detalleViewModel.vehiculo.collectAsState()
    val cargando by detalleViewModel.cargando.collectAsState()

    val idsFavoritos by favoritosViewModel.idsFavoritos.collectAsState()
    val esFavorito = idsFavoritos.contains(vehiculoId)

    val context = LocalContext.current
    var imagenSeleccionadaIndex by remember { mutableIntStateOf(0) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val fechaElegida = "$dayOfMonth/${month + 1}/$year"
            vehiculoState?.let { v ->
                citasViewModel.agendarCita(
                    vehiculoId = vehiculoId,
                    marcaModelo = "${v.marca} ${v.modelo}",
                    fecha = fechaElegida,
                    onSuccess = { Toast.makeText(context, "¡Cita agendada!", Toast.LENGTH_LONG).show() },
                    onError = { error -> Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show() }
                )
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val formatearPrecio: (Double) -> String = { precio ->
        NumberFormat.getNumberInstance(Locale.US).format(precio)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val v = vehiculoState
            if (v != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!v.logoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = v.logoUrl,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp) // Sin ColorFilter
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Agendar", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                val linkFotoVehiculo = v.imagenes.getOrNull(0) ?: ""
                                val msgRaw = "Hola, me interesa este vehículo: ${v.marca} ${v.modelo}.\n" +
                                        "Te adjunto el link para más detalles: $linkFotoVehiculo"
                                val msgEncoded = URLEncoder.encode(msgRaw, "UTF-8")
                                val uri = android.net.Uri.parse("https://wa.me/50249452915?text=$msgEncoded")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("WhatsApp", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            val v = vehiculoState
            if (v != null) {
                Box(modifier = Modifier.fillMaxSize()) {

                    // --- LA MAGIA DEL LOGO DE FONDO CORREGIDA ---
                    if (!v.logoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = v.logoUrl,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.Center).size(350.dp).alpha(0.05f),
                            contentScale = ContentScale.Fit

                        )
                    }

                    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${v.marca} ${v.modelo}", fontSize = 26.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f))

                            Row {
                                IconButton(
                                    onClick = { favoritosViewModel.toggleFavorito(vehiculoId) },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorito",
                                        tint = if (esFavorito) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(onClick = onBackClick, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(36.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                v.imagenes.take(4).forEachIndexed { index, url ->
                                    AsyncImage(
                                        model = url, contentDescription = null,
                                        modifier = Modifier.size(65.dp).clip(RoundedCornerShape(12.dp)).border(2.dp, if (imagenSeleccionadaIndex == index) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent, RoundedCornerShape(12.dp)).clickable { imagenSeleccionadaIndex = index },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            AsyncImage(
                                model = v.imagenes.getOrNull(imagenSeleccionadaIndex) ?: "",
                                contentDescription = null,
                                modifier = Modifier.weight(1f).height(240.dp).clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Text("Especificaciones", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(12.dp))

                        FilaDato("Modelo", v.modelo)
                        FilaDato("Año", v.anio.toString())

                        val tipoSeguro = if (!v.tipo.isNullOrEmpty()) v.tipo.replaceFirstChar { it.uppercase() } else "Vehículo"
                        FilaDato("Tipo", tipoSeguro)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Descripción", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))

                        val textoFinal = v.especificaciones.ifEmpty { "Sin descripción detallada." }
                        Text(
                            text = textoFinal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(30.dp))

                        Text("Precio", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Q${formatearPrecio(v.precio)}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No se pudo cargar el vehículo 😕", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBackClick) {
                            Text("Regresar al Catálogo")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilaDato(titulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = titulo, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = valor, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}