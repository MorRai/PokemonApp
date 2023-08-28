package com.example.pokemonapp.ui.pokemon_detail

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import org.koin.androidx.compose.koinViewModel

@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    navigateBack: () -> Unit
) {
    val viewModel = koinViewModel<PokemonDetailViewModel>()
    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonById(pokemonId)
    }
    val pokemonState = viewModel.pokemonFlow.collectAsState()

    when (pokemonState.value) {
        is Response.Loading -> {
            // Отображение состояния загрузки
            CircularProgressIndicator()
        }
        is Response.Success -> {
            val pokemon = (pokemonState.value as Response.Success<Pokemon>).data
            //и что то отображаем
        }
        is Response.Failure -> {
            val errorMessage = (pokemonState.value as Response.Failure).e.message ?: "Unknown error"
            // Отображение ошибки
            Text("Error: $errorMessage")
        }
    }
}