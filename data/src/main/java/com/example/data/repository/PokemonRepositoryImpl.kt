package com.example.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toDomainModels
import com.example.data.model.PokemonEntity
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


internal class PokemonRepositoryImpl(
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
    private val recipePager: Pager<Int, PokemonEntity>,
) : PokemonRepository {

    override fun getPokemons(): Flow<PagingData<Pokemon>> {
        return recipePager.flow.map { pagingData ->
            pagingData.map { it.toDomainModels() }
        }
    }

    override suspend fun getPokemonDetail(pokemonId: Int): Response<Pokemon> {
        return try {
            val pokemonEntity = withContext(Dispatchers.IO) {
                pokemonDatabase.pokemonDao().getPokemon(pokemonId)
            }
            Response.Success(pokemonEntity.toDomainModels())
        } catch (localDbException: Exception) {
            // Try to fetch data from Retrofit in case of error
            try {
                val pokemonDTO = withContext(Dispatchers.IO) {
                    pokemonService.getPokemon(pokemonId)
                }
                Response.Success(pokemonDTO.toDomainModel())
            } catch (retrofitException: Exception) {
                // Return Response.Failure in case of error
                Response.Failure(retrofitException)
            }
        }
    }


}