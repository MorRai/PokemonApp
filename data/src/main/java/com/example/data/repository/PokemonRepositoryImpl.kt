package com.example.data.repository

import com.example.data.api.PokemonApi
import com.example.data.database.PokemonDatabase
import com.example.data.mapper.toDomainModel
import com.example.data.mapper.toDomainModels
import com.example.data.mapper.toEntityModels
import com.example.data.model.PokemonDTO
import com.example.data.model.PokemonEntity
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers


internal class PokemonRepositoryImpl(
   // private val pokemonPager: Pager<Int, PokemonEntity>,
    private val pokemonService: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
) : PokemonRepository {
    //Fetches paginated Pokemon data
   /* override fun getPokemon(): Flowable<PagingData<Pokemon>> {
        return pokemonPager.flowable
            .map { pagingData ->
                pagingData.map { it.toDomainModels() }
            }
    }*/

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
    // Метод для загрузки покемонов по указанной странице без библиотеки
    private val pageSize  = 20
    override fun loadPokemons(page: Int): Single<Response<List<Pokemon>>> {
        val offset = (page - 1) * pageSize
        return pokemonService.getPokemons(offset, pageSize)
            .flatMap { pokemonsDTO ->
                val pokemonSingleList = pokemonsDTO.results.map { pokemonResponse ->
                    val id = pokemonResponse.url.split("/").let { it[it.size - 2].toInt() }
                    pokemonService.getPokemon(id)
                }
                Single.zip(pokemonSingleList) { pokemons ->
                    pokemons.map { it as PokemonDTO }
                }
            }
            .flatMap { pokemonList ->
                val entities = pokemonList.toDomainModel()
                pokemonDatabase.pokemonDao().insert(entities.toEntityModels()).subscribeOn(Schedulers.io()).subscribe()
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

    // Метод для получения покемонов из локальной базы данных
    override fun getPokemonsFromDatabase(): Single<Response<List<Pokemon>>> {
        return pokemonDatabase.pokemonDao().getPokemons()
            .map{ it.map { it.toDomainModels() } }
            .map<Response<List<Pokemon>>> {
                Response.Success(it)
            }
            .onErrorReturn { throwable ->
                Response.Failure(throwable)
            }
            .subscribeOn(Schedulers.io())

    }

}