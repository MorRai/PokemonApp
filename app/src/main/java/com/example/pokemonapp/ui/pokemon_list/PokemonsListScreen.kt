package com.example.pokemonapp.ui.pokemon_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.domain.model.Pokemon
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.TopAppBar

@Composable
@ExperimentalMaterial3Api
fun ListPokemonsScreen(
    navigateToPokemonDetailScreen: (pokemonId: Int) -> Unit,
) {

    val viewModel = koinViewModel<PokemonsListViewModel>()
    val pokemonFlowState = viewModel.pokemonPagingDataFlow.collectAsLazyPagingItems()
    Column {
        TopAppBar(
            title = {
                Text(
                    text = "Pokemon List",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)

        )

        PokemonList(pokemons = pokemonFlowState, navigateToPokemonDetailScreen)

    }

}


@Composable
@ExperimentalMaterial3Api
fun PokemonCard(
    drawable: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = drawable,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(86.dp, 112.dp)
            )

            Spacer(modifier = Modifier.width(32.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun PokemonList(
    pokemons: LazyPagingItems<Pokemon>,
    navigateToPokemonDetailScreen: (pokemonId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(

        modifier = modifier
            .background(Color.LightGray)
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {

        items(pokemons.itemCount) { index ->
            pokemons[index]?.let {
                PokemonCard(
                    it.image, it.name,
                    onClick = {
                        navigateToPokemonDetailScreen(it.id)
                    }, modifier = Modifier
                )
            }
        }

        val loadState = pokemons.loadState.mediator
        item {
            if (loadState?.refresh == LoadState.Loading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "Refresh Loading"
                    )

                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            if (loadState?.append == LoadState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            if (loadState?.refresh is LoadState.Error || loadState?.append is LoadState.Error) {
                val isPaginatingError =
                    (loadState.append is LoadState.Error) || pokemons.itemCount > 1
                val error = if (loadState.append is LoadState.Error)
                    (loadState.append as LoadState.Error).error
                else
                    (loadState.refresh as LoadState.Error).error

                val modifierIn = if (isPaginatingError) {
                    Modifier.padding(8.dp)
                } else {
                    Modifier.fillMaxSize()
                }
                Column(
                    modifier = modifierIn,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (!isPaginatingError) {
                        Icon(
                            modifier = Modifier
                                .size(64.dp),
                            imageVector = Icons.Rounded.Warning, contentDescription = null
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = error.message ?: error.toString(),
                        textAlign = TextAlign.Center,
                    )

                    Button(
                        onClick = {
                            pokemons.refresh()
                        },
                        content = {
                            Text(text = "Refresh")
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                        )
                    )
                }
            }
        }
    }
}

