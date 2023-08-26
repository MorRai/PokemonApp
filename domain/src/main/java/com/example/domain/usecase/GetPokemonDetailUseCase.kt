package com.example.domain.usecase

import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Single

class GetPokemonDetailUseCase(private val pokemonRepository: PokemonRepository) {
    operator fun invoke(pokemonId: Int): Single<Response<Pokemon>> {

         return   pokemonRepository.getPokemonDetail(pokemonId)

    }
}