package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface PokemonRepository {
    fun getPokemon(): Flowable<PagingData<Pokemon>>
    fun getPokemonDetail(pokemonId: Int): Single<Response<Pokemon>>
}