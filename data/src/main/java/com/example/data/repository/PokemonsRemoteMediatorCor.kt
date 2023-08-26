package com.example.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState.Loading.endOfPaginationReached
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.rxjava3.RxRemoteMediator
import androidx.room.withTransaction
import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toEntityModels
import com.example.data.model.PokemonEntity
import com.example.data.model.PokemonRemoteKeys
import com.example.domain.model.Pokemon
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException
import java.util.concurrent.TimeUnit
/*
@OptIn(ExperimentalPagingApi::class)
internal class PokemonsRemoteMediatorCor(
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : RemoteMediator<Int, PokemonEntity>() {

    val pageSize = 20 //вынести в параметр

    companion object{
        const val INVALID_PAGE = 1
    }
    //override fun initializeSingle(): Single<InitializeAction> {
      //  val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

       // return if (System.currentTimeMillis() - (pokemonDatabase.pokemonRemoteKeysDao()
      //          .getCreationTime() ?: 0) < cacheTimeout
      //  ) {
            //можно еще чек интеренета добаваить и не занулять если его нет
      //      Single.just(InitializeAction.SKIP_INITIAL_REFRESH)
      //  } else {
      //      Single.just(InitializeAction.LAUNCH_INITIAL_REFRESH)
      //  }
  //  }

    /*override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)

        return if (System.currentTimeMillis() - (pokemonDatabase.pokemonRemoteKeysDao().getCreationTime() ?: 0) < cacheTimeout) {
            //можно еще чек интеренета добаваить и не занулять если его нет
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }*/


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

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                //New Query so clear the DB
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val recipes =  pokemonService.getPokemons(
                offset = (page-1) * pageSize,
                limit = pageSize).results.map { result ->
                    val id = result.url.split("/").let { it[it.size - 2].toInt() }
                    val pokemonDetailsResponse = pokemonService.getPokemon(id)
                    pokemonDetailsResponse.toDomainModel()
                }

            val endOfPaginationReached = recipes.isEmpty()

            pokemonDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pokemonDatabase.pokemonRemoteKeysDao().clearRemoteKeys()
                    pokemonDatabase.pokemonDao().clearAllPokemons()
                }
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = recipes.map {
                    PokemonRemoteKeys(
                        pokemonID = it.id,
                        prevKey = prevKey,
                        currentPage = page,
                        nextKey = nextKey
                    )
                }
                pokemonDatabase.pokemonRemoteKeysDao().insertAll(remoteKeys)
                pokemonDatabase.pokemonDao().insert(recipes.toEntityModels())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (error: IOException) {
            return MediatorResult.Error(error)
        } catch (error: HttpException) {
            return MediatorResult.Error(error)
        }

    }

    @Suppress("DEPRECATION")
    private fun insertToDb(page: Int, loadType: LoadType, data: List<Pokemon>,endOfPaginationReached:Boolean ): List<Pokemon> {
        pokemonDatabase.beginTransaction()

        try {
            if (loadType == LoadType.REFRESH) {
                pokemonDatabase.pokemonRemoteKeysDao().clearRemoteKeys()
                pokemonDatabase.pokemonDao().clearAllPokemons()
            }

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val remoteKeys = data.map {
                PokemonRemoteKeys(
                    pokemonID = it.id,
                    prevKey = prevKey,
                    currentPage = page,
                    nextKey = nextKey
                )
            }
            pokemonDatabase.pokemonRemoteKeysDao().insertAll(remoteKeys)
            pokemonDatabase.pokemonDao().insert(data.toEntityModels())
            pokemonDatabase.setTransactionSuccessful()

        } finally {
            pokemonDatabase.endTransaction()
        }

        return data
    }

}*/
val gd = 1