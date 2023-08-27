package com.example.pokemonapp.Intents

sealed class PokemonDetailIntent {
    object Load : PokemonDetailIntent()
}