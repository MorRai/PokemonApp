package com.example.pokemonapp.Intents

sealed class ListPokemonIntent {
    object InitialLoad : ListPokemonIntent()
}