package com.example.data.di

import com.example.domain.usecase.GetPokemonDetailUseCase
import com.example.domain.usecase.GetPokemonsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val useCaseModule = module {
    factoryOf(::GetPokemonDetailUseCase)
    factoryOf(::GetPokemonsUseCase)
}