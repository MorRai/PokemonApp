package com.example.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.example.data.model.PokemonEntity
@Dao
internal interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemons: List<PokemonEntity>)

    @Update
    suspend fun update(pokemon: PokemonEntity)

    @Delete
    suspend fun delete(pokemon: PokemonEntity)

    @Query("SELECT * from pokemons_table WHERE id = :id")
    suspend fun getPokemon(id: Int): PokemonEntity

    @Query("SELECT * from pokemons_table")
    fun getPokemons(): PagingSource<Int, PokemonEntity>

    @Query("Delete From pokemons_table")
    suspend fun clearAllPokemons()

}