package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.PokemonEntity
import com.example.data.model.PokemonRemoteKeys

@Database(entities = [PokemonEntity::class, PokemonRemoteKeys::class], version = 1, exportSchema = false)
internal abstract class PokemonDatabase: RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao

    abstract fun pokemonRemoteKeysDao(): PokemonRemoteKeysDao
}