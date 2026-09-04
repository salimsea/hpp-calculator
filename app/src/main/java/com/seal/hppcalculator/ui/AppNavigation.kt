package com.seal.hppcalculator.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.seal.hppcalculator.data.repository.HppRepository
import com.seal.hppcalculator.ui.screen.CreateHppScreen
import com.seal.hppcalculator.ui.screen.DataHppScreen
import com.seal.hppcalculator.ui.screen.InspirasiResepScreen
import com.seal.hppcalculator.ui.screen.MainScreen
import com.seal.hppcalculator.ui.screen.MarkupOjolScreen
import com.seal.hppcalculator.ui.screen.OnboardingScreen
import com.seal.hppcalculator.ui.screen.ResultScreen
import com.seal.hppcalculator.ui.screen.SplashScreen
import com.seal.hppcalculator.ui.screen.TargetImpasBepScreen
import com.seal.hppcalculator.viewmodel.HppViewModel
import com.seal.hppcalculator.viewmodel.HppViewModelFactory

@Composable
fun AppNavigation(
    repository: HppRepository,
    startDestination: String = "home",
    onOnboardingFinished: () -> Unit = {}
) {
    val navController = rememberNavController()
    val viewModel: HppViewModel = viewModel(factory = HppViewModelFactory(repository))

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                nextDestination = startDestination,
                onTimeout = { next ->
                    navController.navigate(next) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    onOnboardingFinished()
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            MainScreen(
                viewModel = viewModel,
                onNavigateToCreate = { navController.navigate("create") },
                onNavigateToResult = { id -> navController.navigate("result/$id") },
                onNavigateToMarkupOjol = { navController.navigate("markup_ojol") },
                onNavigateToTargetBep = { navController.navigate("target_impas_bep") },
                onNavigateToDataHpp = { navController.navigate("data_hpp") },
                onResetAppToOnboarding = {
                    navController.navigate("onboarding") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("data_hpp") {
            DataHppScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { id -> navController.navigate("result/$id") },
                onNavigateToCreate = { navController.navigate("create") }
            )
        }
        composable("create") {
            CreateHppScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { navController.navigate("home") } // For now, just save goes to home
            )
        }
        composable(
            "result/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            ResultScreen(
                viewModel = viewModel,
                productId = id,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToEdit = { navController.navigate("create") }
            )
        }
        composable("markup_ojol") {
            MarkupOjolScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("target_impas_bep") {
            TargetImpasBepScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("inspirasi_resep") {
            InspirasiResepScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCreate = { navController.navigate("create") },
                onNavigateToResult = { id -> navController.navigate("result/$id") }
            )
        }
    }
}
