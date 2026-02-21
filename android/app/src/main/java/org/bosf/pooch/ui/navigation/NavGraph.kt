package org.bosf.pooch.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.bosf.pooch.ui.auth.LoginScreen
import org.bosf.pooch.ui.auth.RegisterScreen
import org.bosf.pooch.ui.dogs.DogDetailScreen
import org.bosf.pooch.ui.dogs.DogFormScreen
import org.bosf.pooch.ui.dogs.DogsScreen
import org.bosf.pooch.ui.history.ScanHistoryScreen
import org.bosf.pooch.ui.home.HomeScreen
import org.bosf.pooch.ui.product.ProductDetailScreen
import org.bosf.pooch.ui.scanner.ScannerScreen

@Composable
fun PoochScanNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToScanner   = { navController.navigate(Screen.Scanner.route) },
                onNavigateToDogs      = { navController.navigate(Screen.Dogs.route) },
                onNavigateToHistory   = { dogId -> navController.navigate(Screen.ScanHistory.createRoute(dogId)) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dogs.route) {
            DogsScreen(
                onNavigateToAddDog    = { navController.navigate(Screen.AddDog.route) },
                onNavigateToDogDetail = { dogId -> navController.navigate(Screen.DogDetail.createRoute(dogId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AddDog.route) {
            DogFormScreen(
                dogId = null,
                onSaved = { navController.popBackStack() },
                onBack  = { navController.popBackStack() }
            )
        }

        composable(Screen.EditDog.route) { backStack ->
            val dogId = backStack.arguments?.getString("dogId")

            DogFormScreen(
                dogId = dogId,
                onSaved = { navController.popBackStack() },
                onBack  = { navController.popBackStack() }
            )
        }

        composable(Screen.DogDetail.route) { backStack ->
            val dogId = backStack.arguments?.getString("dogId") ?: return@composable

            DogDetailScreen(
                dogId = dogId,
                onNavigateToEdit    = { navController.navigate(Screen.EditDog.createRoute(dogId)) },
                onNavigateToHistory = { navController.navigate(Screen.ScanHistory.createRoute(dogId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Scanner.route) {
            ScannerScreen(
                onScanSuccess = { scanId -> navController.navigate(Screen.ProductDetail.createRoute(scanId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ProductDetail.route) { backStack ->
            val scanId = backStack.arguments?.getString("scanId") ?: return@composable

            ProductDetailScreen(
                scanId = scanId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ScanHistory.route) { backStack ->
            val dogId = backStack.arguments?.getString("dogId") ?: return@composable

            ScanHistoryScreen(
                dogId = dogId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
