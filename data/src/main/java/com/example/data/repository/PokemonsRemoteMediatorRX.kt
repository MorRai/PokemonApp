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

    val pageSize = 20 //вынести в параметр

    companion object {
        const val INVALID_PAGE = 0
    }

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

    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>,
    ): Single<MediatorResult> {
        val pageSingle: Single<Int> = when (loadType) {
            LoadType.REFRESH -> {
                Log.e("page", "REFRESH")
                getRemoteKeyClosestToCurrentPosition(state)
                    .map { remoteKeys ->
                        remoteKeys.orElse(null)?.nextKey?.minus(1) ?: 1
                    }
            }
            LoadType.PREPEND -> {
                Log.e("page", "PREPEND")
                getRemoteKeyForFirstItem(state)
                    .map { remoteKeys ->
                        remoteKeys.orElse(null)?.prevKey ?: INVALID_PAGE
                    }

            }
            LoadType.APPEND -> {
                Log.e("page", "APPEND")
                getRemoteKeyForLastItem(state)
                    .map { remoteKeys ->
                        remoteKeys.orElse(null)?.nextKey ?: INVALID_PAGE
                    }
            }
        }

        return pageSingle.flatMap { page ->
            Log.e("page", page.toString())
            if (page == INVALID_PAGE) {
                Single.just(MediatorResult.Success(endOfPaginationReached = true) as MediatorResult)
            } else {
                pokemonService.getPokemons(
                    offset = (page - 1) * pageSize,
                    limit = pageSize
                ).subscribeOn(Schedulers.io())
                    .flatMap { pokemonsDTO ->
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
                        Log.e("page", endOfPaginationReached.toString())
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
                        Log.e("page", remoteKeys.toString())
                        pokemonDatabase.pokemonRemoteKeysDao().insertAll(remoteKeys)
                            .subscribeOn(Schedulers.io())
                            .onErrorReturn {
                                Log.e("page", remoteKeys.toString())
                            }
                            .subscribe()
                        pokemonDatabase.pokemonDao()
                            .insert(pokemonList.toDomainModel().toEntityModels())
                            .subscribeOn(Schedulers.io()).subscribe()
                        Single.just(MediatorResult.Success(endOfPaginationReached = endOfPaginationReached))
                    }
            }
        }.onErrorReturn {
            MediatorResult.Error(it)
        }
    }

}