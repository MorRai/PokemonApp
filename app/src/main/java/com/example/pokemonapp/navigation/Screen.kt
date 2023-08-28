package com.example.pokemonapp.navigation

sealed class Screen(val route: String) {
    object ListPokemonsScreen: Screen("Pokemons")
    object PokemonDetailScreen: Screen("Pokemon")
}
