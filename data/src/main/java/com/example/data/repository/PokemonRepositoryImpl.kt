package com.example.data.repository

import androidx.paging.PagingData
import com.example.domain.model.Pokemon
import com.example.domain.repository.PokemonRepository
import io.reactivex.rxjava3.core.Flowable

class PokemonRepositoryImpl:PokemonRepository {
    override fun getPokemon(): Flowable<PagingData<Pokemon>> {
        TODO("Not yet implemented")
    }
}