package com.example.data.mapper

import com.example.data.model.PokemonDTO
import com.example.data.model.PokemonEntity
import com.example.domain.model.Pokemon

internal fun PokemonDTO.toDomainModel(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        type = types?.map{ it.type.name }?.joinToString(", ")?:"",
        image = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png",
        weight = weight,
        height = height
    )
}

internal fun List<Pokemon>.toEntityModels(): List<PokemonEntity> {
    return map { it.toEntityModels() }
}

internal fun List<PokemonDTO>.toDomainModel(): List<Pokemon> {
    return map { it.toDomainModel() }
}

internal fun Pokemon.toEntityModels(): PokemonEntity {
    return PokemonEntity(
        id = id,
        name = name,
        type = type,
        image = image,
        weight = weight,
        height = height
    )
}

internal fun PokemonEntity.toDomainModels(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        type = type,
        image = image,
        weight = weight,
        height = height
    )
}