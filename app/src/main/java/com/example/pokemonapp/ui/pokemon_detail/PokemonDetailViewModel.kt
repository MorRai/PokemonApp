package com.example.pokemonapp.ui.pokemon_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.usecase.GetPokemonDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
) : ViewModel() {

    private val _pokemonFlow = MutableStateFlow<Response<Pokemon>>(Response.Loading)
    val pokemonFlow: StateFlow<Response<Pokemon>> = _pokemonFlow

    fun loadPokemonById(pokemonId: Int) {
        viewModelScope.launch {
            val pokemon = getPokemonDetailUseCase(pokemonId)
            _pokemonFlow.value = pokemon
        }
    }

}



