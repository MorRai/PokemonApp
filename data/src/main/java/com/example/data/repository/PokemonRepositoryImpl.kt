package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toDomainModels
import com.example.data.model.PokemonEntity
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


internal class PokemonRepositoryImpl(
    private val pokemonPager: Pager<Int, PokemonEntity>,
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : PokemonRepository {
    //Fetches paginated Pokemon data
    override fun getPokemon(): Flowable<PagingData<Pokemon>> {
        return pokemonPager.flowable
            .map { pagingData ->
                pagingData.map { it.toDomainModels() }
            }
    }

    //Fetches detailed information about a specific Pokemon by its ID
    override fun getPokemonDetail(pokemonId: Int): Single<Response<Pokemon>> {
        return pokemonDatabase.pokemonDao().getPokemon(pokemonId)
            .subscribeOn(Schedulers.io())
            .map { pokemonEntity ->
                Response.Success(pokemonEntity.toDomainModels()) as Response<Pokemon>
            }
            .onErrorResumeNext {
                // Try to fetch data from Retrofit in case of error
                val retrofitFlowable = pokemonService.getPokemon(pokemonId)
                    .map { pokemonDTO ->
                        Response.Success(pokemonDTO.toDomainModel()) as Response<Pokemon>
                    }
                    .onErrorReturn { e ->
                        // Return Response.Failure in case of error
                        Response.Failure(e)
                    }
                retrofitFlowable
            }

    }

}