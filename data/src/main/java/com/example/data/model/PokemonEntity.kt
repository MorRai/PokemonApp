package com.example.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons_table")

internal data class PokemonEntity(
    @PrimaryKey @NonNull
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "weight")
    val weight: Int,
    @ColumnInfo(name = "height")
    val height: Int
)