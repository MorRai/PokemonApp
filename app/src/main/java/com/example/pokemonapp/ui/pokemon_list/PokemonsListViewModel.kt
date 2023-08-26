package com.example.pokemonapp.ui.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import com.example.domain.model.Pokemon
import com.example.domain.usecase.GetPokemonsUseCase
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi


class PokemonsListViewModel(getPokemonsUseCase: GetPokemonsUseCase) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val pokemonPagingDataFlow: Flowable<PagingData<Pokemon>> = getPokemonsUseCase.invoke().onBackpressureBuffer()
        .cachedIn(viewModelScope)

}