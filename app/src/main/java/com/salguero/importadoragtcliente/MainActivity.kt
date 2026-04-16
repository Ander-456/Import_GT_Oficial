package com.salguero.importadoragtcliente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.salguero.importadoragtcliente.presentation.ui.PerfilScreen
import com.salguero.importadoragtcliente.presentation.ui.theme.ImportGTTheme
import com.salguero.importadoragtcliente.presentation.ui.theme.InicioScreen
import com.salguero.importadoragtcliente.presentation.ui.theme.LoginScreen
import com.salguero.importadoragtcliente.presentation.ui.theme.RegistroScreen


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
                    color = MaterialTheme.colorScheme.background // Fondo adaptable
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

    // Validamos Inicio y Perfil para mostrar la barra
    val esInicio = currentRoute == "inicio"
    val esPerfil = currentRoute == "perfil"
    val mostrarBarra = esInicio || esPerfil

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface, // Se adapta al modo oscuro
                    contentColor = MaterialTheme.colorScheme.onSurface
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
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer)
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
                        colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer)
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
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
                    onNavigateToCatalogo = { /* TODO */ },
                    onNavigateToCitas = { /* TODO */ },
                    onNavigateToFinanciamiento = { /* TODO */ },
                    onNavigateToVehiculoDetalle = { id -> /* TODO */ }
                )
            }

            composable("perfil") {
                PerfilScreen(
                    onCerrarSesionSuccess = {
                        navController.navigate("login") { popUpTo(0) }
                    }
                )
            }
        }
    }
}