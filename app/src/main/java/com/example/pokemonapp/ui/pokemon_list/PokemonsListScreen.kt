package com.example.pokemonapp.ui.pokemon_list

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import org.koin.androidx.compose.koinViewModel

@Composable
@ExperimentalMaterial3Api
fun ListPokemonsScreen(
    navigateToPokemonDetailScreen: (pokemonId: Int) -> Unit
) {

    val viewModel = koinViewModel<PokemonsListViewModel>()


}