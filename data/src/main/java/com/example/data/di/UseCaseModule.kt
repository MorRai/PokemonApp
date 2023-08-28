package com.example.data.di

import com.example.domain.usecase.GetPokemonDetailUseCase
import com.example.domain.usecase.GetPokemonsFromCache
import com.example.domain.usecase.GetPokemonsUseCase
import com.example.domain.usecase.LoadPokemonUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val useCaseModule = module {
    factoryOf(::GetPokemonsUseCase)
    factoryOf(::GetPokemonDetailUseCase)
    factoryOf(::GetPokemonsFromCache)
    factoryOf(::LoadPokemonUseCase)
}