package org.bosf.pooch.ui.navigation

sealed class Screen(val route: String) {
    data object Login          : Screen("login")

    data object Register       : Screen("register")

    data object Home           : Screen("home")

    data object Dogs           : Screen("dogs")

    data object AddDog         : Screen("dogs/add")

    data object EditDog        : Screen("dogs/{dogId}/edit") {
        fun createRoute(dogId: String) = "dogs/$dogId/edit"
    }

    data object DogDetail      : Screen("dogs/{dogId}") {
        fun createRoute(dogId: String) = "dogs/$dogId"
    }

    data object Scanner        : Screen("scanner")

    data object ProductDetail  : Screen("product/{scanId}") {
        fun createRoute(scanId: String) = "product/$scanId"
    }

    data object ScanHistory    : Screen("history/{dogId}") {
        fun createRoute(dogId: String) = "history/$dogId"
    }
}