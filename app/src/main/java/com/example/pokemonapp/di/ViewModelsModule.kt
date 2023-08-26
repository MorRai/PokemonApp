package com.example.pokemonapp.di

import com.example.pokemonapp.ui.pokemon_detail.PokemonDetailViewModel
import com.example.pokemonapp.ui.pokemon_list.PokemonsListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelsModule = module {
    viewModelOf(::PokemonsListViewModel)
    viewModelOf(::PokemonDetailViewModel)
}