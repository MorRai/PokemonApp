package com.example.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.PokemonEntity

@Database(entities = [PokemonEntity::class], version = 1, exportSchema = false)
internal abstract class PokemonDatabase: RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao

    abstract fun pokemonRemoteKeysDao(): PokemonRemoteKeysDao
}