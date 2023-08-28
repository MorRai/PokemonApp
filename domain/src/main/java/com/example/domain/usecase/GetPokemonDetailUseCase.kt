package com.example.domain.usecase

import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.repository.PokemonRepository

class GetPokemonDetailUseCase(private val pokemonRepository: PokemonRepository) {

    suspend operator fun invoke(pokemonId: Int): Response<Pokemon> {
        return pokemonRepository.getPokemonDetail(pokemonId)
    }
}