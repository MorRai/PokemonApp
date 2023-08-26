package com.example.data.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.data.database.PokemonDatabase
import com.example.data.repository.PokemonsRemoteMediatorRX

import org.koin.dsl.module

internal val pokemonMediatorModule = module {
    single{
        @OptIn(ExperimentalPagingApi::class)
        Pager(
        config = PagingConfig(
            pageSize = 20, prefetchDistance = 10,
            initialLoadSize = 20,
        ),
        pagingSourceFactory = { get<PokemonDatabase>().pokemonDao().getPokemons() },
        remoteMediator = PokemonsRemoteMediatorRX(get(), get()),
    )

    }
}