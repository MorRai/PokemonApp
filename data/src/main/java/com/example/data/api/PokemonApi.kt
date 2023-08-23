package com.example.data.api

import com.example.data.model.PokemonDTO
import com.example.data.model.PokemonsDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PokemonApi {
    @GET("pokemon/{id}")
    suspend fun getPokemon(
        @Path("id") id: Int,
    ): PokemonDTO

    @GET("pokemon")
    suspend fun getPokemons(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): PokemonsDTO

}