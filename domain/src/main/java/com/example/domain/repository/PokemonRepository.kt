package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.Pokemon
import io.reactivex.rxjava3.core.Flowable

interface PokemonRepository {
    fun getPokemon(): Flowable<PagingData<Pokemon>>
}