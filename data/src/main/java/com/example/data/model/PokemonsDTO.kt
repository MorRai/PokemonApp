package com.example.data.model

internal data class PokemonsDTO(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResponse>
)

internal data class PokemonDTO(
    val id: Int,
    val name: String,
    val image: String,
    val types: List<TypeResponse>?,
    val weight: Int,
    val height: Int
)

data class PokemonResponse(
    val name: String,
    val url: String
)

data class TypeResponse(
    val type: TypeDetailsResponse
)

data class TypeDetailsResponse(
    val name: String
)