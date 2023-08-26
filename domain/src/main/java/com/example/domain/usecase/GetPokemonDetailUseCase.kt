package com.example.domain.usecase

import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Flowable

class GetPokemonDetailUseCase(private val pokemonRepository: PokemonRepository) {
    operator fun invoke(pokemonId: Int): Flowable<Response<Pokemon>> {

         return   pokemonRepository.getPokemonDetail(pokemonId)

    }
}