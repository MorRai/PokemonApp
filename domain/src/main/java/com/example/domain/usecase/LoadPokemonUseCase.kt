package com.example.domain.usecase

import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Single


class LoadPokemonUseCase(private val pokemonRepository: PokemonRepository) {
    operator fun invoke(page: Int): Single<Response<List<Pokemon>>> {
        return   pokemonRepository.loadPokemons(page)
    }
}