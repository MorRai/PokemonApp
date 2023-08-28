package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PokemonRemoteKeys
import java.util.*

@Dao
interface PokemonRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<PokemonRemoteKeys>)

    @Query("Select * From pokemon_remote_key Where pokemon_id = :id")
    suspend fun getRemoteKeyByPokemonID(id: Int): PokemonRemoteKeys?

    @Query("Delete From pokemon_remote_key")
    suspend fun clearRemoteKeys()

    @Query("Select created_at From pokemon_remote_key Order By created_at DESC LIMIT 1")
    suspend fun getCreationTime(): Long?

}