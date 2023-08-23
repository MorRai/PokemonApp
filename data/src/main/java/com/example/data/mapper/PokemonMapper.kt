package com.example.data.mapper

import com.example.data.model.PokemonDTO
import com.example.data.model.PokemonEntity
import com.example.domain.model.Pokemon

internal fun PokemonDTO.toDomainModel(image: String): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        type = types.map{ it.type.name }.joinToString(", "),
        image = image,
        weight = weight,
        height = height
    )
}


internal fun Pokemon.toDomainModels(): PokemonEntity {
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