package com.example.domain.usecase

import androidx.paging.PagingData
import com.example.domain.model.Pokemon
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Flowable

class GetPokemonsUseCase(private val pokemonRepository: PokemonRepository) {
   // operator fun invoke(): Flowable<PagingData<Pokemon>> {
        //return pokemonRepository.getPokemon()
   // }
}