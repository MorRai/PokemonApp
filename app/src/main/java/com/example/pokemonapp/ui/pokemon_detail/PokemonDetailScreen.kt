package com.example.pokemonapp.ui.pokemon_detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.TopAppBar

@Composable
@ExperimentalMaterial3Api
fun PokemonDetailScreen(
    pokemonId: Int,
    navigateBack: () -> Unit,
) {
    val viewModel = koinViewModel<PokemonDetailViewModel>()
    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonById(pokemonId)
    }
    val pokemonState = viewModel.pokemonFlow.collectAsState()
    Column {

        TopAppBar(
            title = {
                Text(
                    text = "Pokemon Details",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer),
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (pokemonState.value) {
                is Response.Loading -> {
                    // Отображение состояния загрузки
                    CircularProgressIndicator()
                }
                is Response.Success -> {
                    val pokemon = (pokemonState.value as Response.Success<Pokemon>).data
                    PokemonDetailsContent(pokemon)
                }
                is Response.Failure -> {
                    val errorMessage =
                        (pokemonState.value as Response.Failure).e.message ?: "Unknown error"
                    // Отображение ошибки
                    Text("Error: $errorMessage")
                }
            }
        }
    }
}


@Composable
fun PokemonDetailsContent(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = pokemon.image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(200.dp).width(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Type: ${pokemon.type}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Weight: ${pokemon.weight} kg",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Height: ${pokemon.height} m",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}