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
import androidx.compose.material.icons.filled.Home
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
import com.salguero.importadoragtcliente.presentation.ui.CatalogoScreen
import com.salguero.importadoragtcliente.presentation.ui.DetalleScreen
import com.salguero.importadoragtcliente.presentation.ui.PerfilScreen
import com.salguero.importadoragtcliente.presentation.ui.theme.ImportGTTheme
import com.salguero.importadoragtcliente.presentation.ui.theme.InicioScreen
import com.salguero.importadoragtcliente.presentation.ui.theme.LoginScreen
import com.salguero.importadoragtcliente.presentation.ui.theme.RegistroScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Suscripción a notificaciones push de Firebase
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

    // Validamos en qué pantallas debe ser visible la barra de navegación inferior
    val esInicio = currentRoute == "inicio"
    val esPerfil = currentRoute == "perfil"
    val mostrarBarra = esInicio || esPerfil

    // Usamos un Scaffold vacío para manejar el padding general del sistema (como el notch)
    Scaffold { paddingValues ->

        // Arquitectura de Capas (Z-Index) para permitir que el contenido fluya detrás de la barra flotante
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- CAPA 1 (FONDO): EL NAVEGADOR Y LAS PANTALLAS ---
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.fillMaxSize() // Ocupa el 100% de la pantalla
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
                        onNavigateToCitas = { /* TODO: Fase 4 */ },
                        onNavigateToFinanciamiento = { /* TODO: Fase 4 */ },
                        onNavigateToVehiculoDetalle = { id -> navController.navigate("detalle/$id") }
                    )
                }
                composable("perfil") {
                    PerfilScreen(
                        onCerrarSesionSuccess = { navController.navigate("login") { popUpTo(0) } }
                    )
                }

                // Ruta del catálogo con soporte para parámetros opcionales (Filtro por financiamiento)
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

            // --- CAPA 2 (FRENTE): LA BARRA DE NAVEGACIÓN FLOTANTE ---
            if (mostrarBarra) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter) // Anclada en la parte inferior
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 24.dp) // Espaciado para el efecto flotante
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        shadowElevation = 16.dp, // Sombra profunda para destacar sobre el contenido
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            tonalElevation = 0.dp,
                            windowInsets = WindowInsets(0, 0, 0, 0), // Remueve los márgenes por defecto
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            NavigationBarItem(
                                selected = esPerfil,
                                onClick = {
                                    navController.navigate("perfil") {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                                label = { Text("Perfil") },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}