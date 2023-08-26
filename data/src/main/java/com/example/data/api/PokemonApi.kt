package com.example.data.api

import com.example.data.model.PokemonDTO
import com.example.data.model.PokemonsDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PokemonApi {
    @GET("pokemon/{id}")
     fun getPokemon(
        @Path("id") id: Int,
    ):Single<PokemonDTO>

    @GET("pokemon")
     fun getPokemons(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Single<PokemonsDTO>

}