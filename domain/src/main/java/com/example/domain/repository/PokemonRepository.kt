package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokemonDetail(pokemonId: Int): Response<Pokemon>
    fun getPokemons(): Flow<PagingData<Pokemon>>
}