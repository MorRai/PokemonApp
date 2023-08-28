package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.Pokemon
import com.example.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetPokemonsUseCase(private val pokemonRepository: PokemonRepository) {
    operator fun invoke() : Flow<PagingData<Pokemon>> {
        return pokemonRepository.getPokemons()
            .flowOn(Dispatchers.IO)
    }
}
