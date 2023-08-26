package com.example.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.example.data.model.PokemonEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
internal interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(pokemons: List<PokemonEntity>): Completable

    @Update
    fun update(pokemon: PokemonEntity): Completable

    @Delete
    fun delete(pokemon: PokemonEntity): Completable

    @Query("SELECT * from pokemons_table WHERE id = :id")
    fun getPokemon(id: Int): Single<PokemonEntity>

    @Query("SELECT * from pokemons_table")
    fun getPokemons(): PagingSource<Int,PokemonEntity>

    @Query("Delete From pokemons_table")
    fun clearAllPokemons(): Completable

}