package com.example.data.di

import com.example.data.repository.PokemonRepositoryImpl
import com.example.domain.repository.PokemonRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val pokemonRepositoryModule = module {
    singleOf(::PokemonRepositoryImpl){bind<PokemonRepository>() }
}
