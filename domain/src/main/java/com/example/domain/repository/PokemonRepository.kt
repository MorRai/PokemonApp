package com.example.domain.repository

import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import io.reactivex.rxjava3.core.Single

interface PokemonRepository {
    fun getPokemonDetail(pokemonId: Int): Single<Response<Pokemon>>

    fun loadPokemons(page: Int): Single<Response<List<Pokemon>>>

    fun getPokemonsFromDatabase(): Single<Response<List<Pokemon>>>
}