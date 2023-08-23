package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class PokemonRemoteKeys( @PrimaryKey(autoGenerate = false)
                       @ColumnInfo(name = "pokemon_id")
                       val pokemonID: Int,
                       val prevKey: Int?,
                       val currentPage: Int,
                       val nextKey: Int?,
                       @ColumnInfo(name = "created_at")
                       val createdAt: Long = System.currentTimeMillis())
