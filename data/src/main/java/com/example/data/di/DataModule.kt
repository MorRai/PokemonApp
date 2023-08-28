package com.example.data.di

import org.koin.dsl.module

val dataModule = module {
    includes(
        pokemonDatabaseModule,
        pokemonRepositoryModule,
        networkModule,
        useCaseModule,
        networkCheckModule
        )
}