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
import io.reactivex.rxjava3.schedulers.Schedulers


internal class PokemonRepositoryImpl(
    private val pokemonPager: Pager<Int, PokemonEntity>,
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : PokemonRepository {
    override fun getPokemon(): Flowable<PagingData<Pokemon>> {
        return pokemonPager.flowable
            .map { pagingData ->
                pagingData.map { it.toDomainModels() }
            }.subscribeOn(Schedulers.io())
    }


    override fun getPokemonDetail(pokemonId: Int): Flowable<Response<Pokemon>> {
        return pokemonDatabase.pokemonDao().getPokemon(pokemonId)
            .map { pokemonEntity ->
                // Преобразовать PokemonEntity в Response.Success<Pokemon>
                Response.Success(pokemonEntity.toDomainModels()) as Response<Pokemon>
            }
            .onErrorResumeNext {
                // Попробовать получить данные из Retrofit
                val retrofitFlowable = pokemonService.getPokemon(pokemonId)
                    .map { pokemonDTO ->
                        // Преобразовать PokemonDTO в Response.Success<Pokemon>
                        Response.Success(pokemonDTO.toDomainModel())as Response<Pokemon>
                    }
                    .onErrorReturn { e ->
                        // Вернуть Response.Failure в случае ошибки
                        Response.Failure(e)
                    }

                retrofitFlowable
            }.toFlowable()

    }

}