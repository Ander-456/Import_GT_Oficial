package com.salguero.importadoragtcliente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.messaging.FirebaseMessaging
import com.salguero.importadoragtcliente.presentation.ui.*
import com.salguero.importadoragtcliente.presentation.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().subscribeToTopic("ofertas")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) { }
            }

        setContent {
            ImportGTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definicion de los destinos que muestran la barra de navegacion flotante
    val esInicio = currentRoute == "inicio"
    val esFavoritos = currentRoute == "favoritos"
    val esUbicacion = currentRoute == "ubicacion"
    val esPerfil = currentRoute == "perfil"
    val mostrarBarra = esInicio || esFavoritos || esUbicacion || esPerfil

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Grafo de navegacion principal
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { navController.navigate("inicio") { popUpTo("login") { inclusive = true } } },
                        onNavigateToRegister = { navController.navigate("registro") }
                    )
                }
                composable("registro") {
                    RegistroScreen(
                        onRegistroSuccess = { navController.navigate("inicio") { popUpTo("login") { inclusive = true } } },
                        onNavigateToLogin = { navController.navigate("login") }
                    )
                }
                composable("inicio") {
                    InicioScreen(
                        onNavigateToCatalogo = { navController.navigate("catalogo") },
                        onNavigateToCitas = { /* Pendiente Fase 4 */ },
                        onNavigateToFinanciamiento = { /* Pendiente Fase 4 */ },
                        onNavigateToVehiculoDetalle = { id -> navController.navigate("detalle/$id") }
                    )
                }

                composable("favoritos") {
                    FavoritosScreen(
                        onVehiculoClick = { id -> navController.navigate("detalle/$id") },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // NUEVA RUTA INTEGRADA: UBICACION (GOOGLE MAPS)
                composable("ubicacion") {
                    UbicacionScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable("perfil") {
                    PerfilScreen(
                        onCerrarSesionSuccess = { navController.navigate("login") { popUpTo(0) } }
                    )
                }

                composable(
                    route = "catalogo?maxPrecio={maxPrecio}",
                    arguments = listOf(
                        navArgument("maxPrecio") { type = NavType.FloatType; defaultValue = -1f }
                    )
                ) { backStackEntry ->
                    val maxPrecio = backStackEntry.arguments?.getFloat("maxPrecio") ?: -1f
                    CatalogoScreen(
                        maxPrecio = maxPrecio,
                        onVehiculoClick = { id -> navController.navigate("detalle/$id") },
                        onBackClick = { navController.popBackStack() },
                        onNavigateToPerfil = { navController.navigate("perfil") }
                    )
                }

                composable("detalle/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    DetalleScreen(
                        vehiculoId = id,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            // Barra de Navegacion Flotante con 4 destinos
            if (mostrarBarra) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp) // Reduje un poco el horizontal para que quepan bien los 4 items
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 16.dp,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 0.dp,
                            windowInsets = WindowInsets(0, 0, 0, 0),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // ITEM INICIO
                            NavigationBarItem(
                                selected = esInicio,
                                onClick = {
                                    navController.navigate("inicio") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                                label = { Text("Inicio") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )

                            // ITEM FAVORITOS
                            NavigationBarItem(
                                selected = esFavoritos,
                                onClick = {
                                    navController.navigate("favoritos") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
                                label = { Text("Favoritos") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )

                            // ITEM UBICACION (NUEVO)
                            NavigationBarItem(
                                selected = esUbicacion,
                                onClick = {
                                    navController.navigate("ubicacion") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Ubicación") },
                                label = { Text("Ubicación") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )

                            // ITEM PERFIL
                            NavigationBarItem(
                                selected = esPerfil,
                                onClick = {
                                    navController.navigate("perfil") {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                                label = { Text("Perfil") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}