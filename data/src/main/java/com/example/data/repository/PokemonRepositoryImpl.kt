package com.example.data.repository

import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toDomainModels
import com.example.data.mapper.toEntityModels
import com.example.data.model.PokemonDTO
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


internal class PokemonRepositoryImpl(
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : PokemonRepository {

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


    ///////////////////////////////////////////////////////////////////
    // Method for loading Pokemon based on the specified page without using  libraries
    override fun loadPokemons(page: Int): Single<Response<List<Pokemon>>> {
        val pageSize = 20
        val offset = (page - 1) * pageSize// Calculate the offset on the page
        return pokemonService.getPokemons(offset, pageSize)// Request Pokemon from the server
            .flatMap { pokemonsDTO ->
                val pokemonSingleList = pokemonsDTO.results.map { pokemonResponse ->
                    val id = pokemonResponse.url.split("/").let { it[it.size - 2].toInt() }
                    pokemonService.getPokemon(id) // Request Pokemon information by ID
                }
                Single.zip(pokemonSingleList) { pokemons ->
                    pokemons.map { it as PokemonDTO }
                }
            }
            .flatMap { pokemonList ->
                val entities = pokemonList.toDomainModel()
                // Insert entities into the local database and return a Single
                pokemonDatabase.pokemonDao().insert(entities.toEntityModels())
                    .subscribeOn(Schedulers.io()).subscribe()
                Single.just(entities)
            }
            .map<Response<List<Pokemon>>> {
                Response.Success(it)
            }
            .onErrorReturn { throwable ->
                Response.Failure(throwable)
            }
            .subscribeOn(Schedulers.io())
    }

    // Method for retrieving Pokemon from the local database
    override fun getPokemonsFromDatabase(): Single<Response<List<Pokemon>>> {
        return pokemonDatabase.pokemonDao().getPokemons()
            .map { it.map { it.toDomainModels() } }
            .map<Response<List<Pokemon>>> {
                Response.Success(it)
            }
            .onErrorReturn { throwable ->
                Response.Failure(throwable)
            }
            .subscribeOn(Schedulers.io())

    }
}