package com.example.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toEntityModels
import com.example.data.model.PokemonEntity
import com.example.data.model.PokemonRemoteKeys
import io.reactivex.Single
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
internal class PokemonsRemoteMediator(
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : RxRemoteMediator<Int, PokemonEntity>() {

    val pageSize = 20 //вынести в параметр

    override fun initializeSingle(): Single<InitializeAction> {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

        return if (System.currentTimeMillis() - (pokemonDatabase.pokemonRemoteKeysDao()
                .getCreationTime() ?: 0) < cacheTimeout
        ) {
            //можно еще чек интеренета добаваить и не занулять если его нет
            Single.just(InitializeAction.SKIP_INITIAL_REFRESH)
        } else {
            Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
        }
    }

    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PokemonEntity>): PokemonRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                pokemonDatabase.pokemonRemoteKeysDao().getRemoteKeyByPokemonID(id)
            }
        }
    }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, PokemonEntity>): PokemonRemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { movie ->
            pokemonDatabase.pokemonRemoteKeysDao().getRemoteKeyByPokemonID(movie.id)
        }
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): PokemonRemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { movie ->
            pokemonDatabase.pokemonRemoteKeysDao().getRemoteKeyByPokemonID(movie.id)
        }
    }


    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>,
    ): Single<MediatorResult> {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return Single.just(MediatorResult.Success(endOfPaginationReached = remoteKeys != null))
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return Single.just(MediatorResult.Success(endOfPaginationReached = remoteKeys != null))
            }
        }

        try {
            val pokemons = pokemonService.getPokemons(
                offset = (page - 1) * pageSize,
                limit = pageSize
            ).results
                .map { result ->
                    val id = result.url.split("/").let { it[it.size - 2].toInt() }
                    val details =
                        pokemonService.getPokemon(id) // Fetch Pokemon details using PokemonAPI service
                    val imageUrl =
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png" // URL for the Pokemon image
                    details.toDomainModel(imageUrl)
                }

            val endOfPaginationReached = pokemons.isEmpty()

            pokemonDatabase.runInTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDatabase.pokemonRemoteKeysDao().clearRemoteKeys()
                    pokemonDatabase.pokemonDao().clearAllPokemons()
                }
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = pokemons.map {
                    PokemonRemoteKeys(
                        pokemonID = it.id,
                        prevKey = prevKey,
                        currentPage = page,
                        nextKey = nextKey
                    )
                }
                pokemonDatabase.pokemonRemoteKeysDao().insertAll(remoteKeys)
                pokemonDatabase.pokemonDao().insert(pokemons.toEntityModels())
            }
            return Single.just(MediatorResult.Success(endOfPaginationReached = endOfPaginationReached))
        } catch (error: IOException) {
            return Single.just(MediatorResult.Error(error))
        } catch (error: HttpException) {
            return Single.just(MediatorResult.Error(error))
        }

    }

}