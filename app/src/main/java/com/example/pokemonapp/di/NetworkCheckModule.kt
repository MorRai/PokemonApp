package com.example.pokemonapp.di

import com.example.pokemonapp.util.NetworkConnectivityObserver
import org.koin.dsl.module

val networkCheckModule = module {
    single { NetworkConnectivityObserver(get()) }
}