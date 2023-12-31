package com.example.data.repository

import android.util.Log
import androidx.paging.*
import androidx.paging.rxjava3.RxRemoteMediator
import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toEntityModels
import com.example.data.model.*
import com.example.data.model.PokemonDTO
import com.example.data.model.PokemonEntity
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalPagingApi::class)
internal class PokemonsRemoteMediatorRX(
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : RxRemoteMediator<Int, PokemonEntity>() {

    companion object {
        private const val INVALID_PAGE = -1
        private const val SECOND_PAGE = 2
        private val pageSize = 20
    }

    // Initialize the refresh strategy based on cache time
    override fun initializeSingle(): Single<InitializeAction> {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        return pokemonDatabase.pokemonRemoteKeysDao().getCreationTime()
            .map { optionalTime ->
                val creationTime = optionalTime.orElse(0)
                if (System.currentTimeMillis() - creationTime < cacheTimeout) {
                    InitializeAction.SKIP_INITIAL_REFRESH
                } else {
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                }
            }
            .onErrorReturn {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
    }


    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PokemonEntity>): Single<Optional<PokemonRemoteKeys>> {
        val anchorPosition = state.anchorPosition
        return if (anchorPosition != null) {
            val closestItem = state.closestItemToPosition(anchorPosition)
            closestItem?.id?.let { id ->
                pokemonDatabase.pokemonRemoteKeysDao().getRemoteKeyByPokemonID(id)
            } ?: Single.just(Optional.empty())
        } else {
            Single.just(Optional.empty())
        }.subscribeOn(Schedulers.io())
    }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, PokemonEntity>): Single<Optional<PokemonRemoteKeys>> {
        val firstItem = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
        return if (firstItem != null) {
            pokemonDatabase.pokemonRemoteKeysDao().getRemoteKeyByPokemonID(firstItem.id)
        } else {
            Single.just(Optional.empty())
        }.subscribeOn(Schedulers.io())
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): Single<Optional<PokemonRemoteKeys>> {
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
        return if (lastItem != null) {
            pokemonDatabase.pokemonRemoteKeysDao().getRemoteKeyByPokemonID(lastItem.id)
        } else {
            Single.just(Optional.empty())
        }.subscribeOn(Schedulers.io())
    }

    // Load data based on load type
    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>,
    ): Single<MediatorResult> {
        return when (loadType) {
            // strategy for initial load or manual refresh
            LoadType.REFRESH -> {
                getRemoteKeyClosestToCurrentPosition(state)
                    .map { remoteKeys ->
                        remoteKeys.orElse(null)?.nextKey?.minus(1) ?: 1
                    }
            }
            LoadType.PREPEND -> {
                getRemoteKeyForFirstItem(state)
                    .map { remoteKeys ->
                        remoteKeys.orElse(null)?.prevKey ?: INVALID_PAGE
                    }

            }
            LoadType.APPEND -> {
                getRemoteKeyForLastItem(state)
                    .map {
                        val remoteKeys = it.orElse(null)
                        if (remoteKeys == null) {
                            SECOND_PAGE
                        } else {
                            remoteKeys.nextKey ?: INVALID_PAGE
                        }
                    }
            }
        }.flatMap { page ->
            // Actual data loading and processing
            if (page == INVALID_PAGE) {
                Single.just(MediatorResult.Success(endOfPaginationReached = true) as MediatorResult)
            } else {
                Log.e("page", page.toString())
                // Load data from remote API
                pokemonService.getPokemons(
                    offset = (page - 1) * pageSize,
                    limit = pageSize
                ).subscribeOn(Schedulers.io())
                    .flatMap { pokemonsDTO ->
                        // Fetch individual Pokemon details
                        val pokemonSingleList = pokemonsDTO.results
                            .map { pokemonResponse ->
                                val id = pokemonResponse.url.split("/")
                                    .let { it[it.size - 2].toInt() }
                                pokemonService.getPokemon(id).subscribeOn(Schedulers.io())
                            }
                        Single.zip(pokemonSingleList) { pokemons ->
                            pokemons.map { it as PokemonDTO }
                        }
                    }.flatMap { pokemonList ->
                        val endOfPaginationReached = pokemonList.isEmpty()
                        // Handle data insertion into database
                        if (loadType == LoadType.REFRESH) {
                            pokemonDatabase.pokemonRemoteKeysDao().clearRemoteKeys()
                                .subscribeOn(Schedulers.io()).subscribe()
                            pokemonDatabase.pokemonDao().clearAllPokemons()
                                .subscribeOn(Schedulers.io()).subscribe()
                        }
                        val prevKey = if (page == 1) null else page - 1
                        val nextKey = if (endOfPaginationReached) null else page + 1
                        val remoteKeys = pokemonList.map {
                            PokemonRemoteKeys(
                                pokemonID = it.id,
                                prevKey = prevKey,
                                currentPage = page,
                                nextKey = nextKey
                            )
                        }
                        pokemonDatabase.pokemonRemoteKeysDao().insertAll(remoteKeys)
                            .subscribeOn(Schedulers.io())
                            .subscribe()
                        pokemonDatabase.pokemonDao()
                            .insert(pokemonList.toDomainModel().toEntityModels())
                            .subscribeOn(Schedulers.io()).subscribe()
                        Single.just(MediatorResult.Success(endOfPaginationReached = endOfPaginationReached))
                    }
            }
        }.onErrorReturn {
            // Handle error during data loading
            Log.e("PokemonsRemoteMediatorRX", "Error in loadSingle: ${it.message}")
            MediatorResult.Error(it)
        }
    }
}