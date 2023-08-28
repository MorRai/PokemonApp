package com.example.pokemonapp.ui.pokemon_list


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.model.Pokemon
import com.example.domain.usecase.GetPokemonsUseCase
import kotlinx.coroutines.flow.Flow


class PokemonsListViewModel(
    getPokemonsUseCase: GetPokemonsUseCase,
) : ViewModel() {
    val pokemonPagingDataFlow: Flow<PagingData<Pokemon>> = getPokemonsUseCase.invoke()
        .cachedIn(viewModelScope)
}