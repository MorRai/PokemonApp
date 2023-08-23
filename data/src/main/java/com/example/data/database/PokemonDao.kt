package com.example.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.example.data.model.PokemonEntity

@Dao
internal interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pokemons: List<PokemonEntity>)

    @Update
    fun update(pokemon: PokemonEntity)

    @Delete
    fun delete(pokemon: PokemonEntity)

    @Query("SELECT * from pokemons_table WHERE id = :id")
    fun getPokemon(id: Int): PokemonEntity

    @Query("SELECT * from pokemons_table")
    fun getPokemons(): PagingSource<Int,PokemonEntity>

    @Query("Delete From recipe_database")
    suspend fun clearAllPokemons()

}