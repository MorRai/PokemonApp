package com.example.data.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PokemonRemoteKeys

interface PokemonRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<PokemonRemoteKeys>)

    @Query("Select * From pokemon_remote_key Where recipe_id = :id")
    fun getRemoteKeyByPokemonID(id: Int): PokemonRemoteKeys?

    @Query("Delete From pokemon_remote_key")
    fun clearRemoteKeys()

    @Query("Select created_at From pokemon_remote_key Order By created_at DESC LIMIT 1")
    fun getCreationTime(): Long?

}