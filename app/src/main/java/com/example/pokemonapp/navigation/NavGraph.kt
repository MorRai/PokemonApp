package com.example.pokemonapp.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pokemonapp.ui.pokemon_detail.PokemonDetailScreen
import com.example.pokemonapp.ui.pokemon_list.ListPokemonsScreen

const val POKEMON_ID = "pokemonId"

@ExperimentalMaterial3Api
@Composable
fun NavGraph (
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ListPokemonsScreen.route
    ) {
        composable(
            route = Screen.ListPokemonsScreen.route
        ) {
            ListPokemonsScreen(
                navigateToPokemonDetailScreen = { pokemonId ->
                    navController.navigate(
                        route = "${Screen.PokemonDetailScreen.route}/${pokemonId}"
                    )
                }
            )
        }
        composable(
            route = "${Screen.PokemonDetailScreen.route}/{$POKEMON_ID}",
            arguments = listOf(
                navArgument(POKEMON_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt(POKEMON_ID) ?: 0
            PokemonDetailScreen(
                pokemonId = pokemonId,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}