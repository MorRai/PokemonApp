package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PokemonRemoteKeys
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*

@Dao
interface PokemonRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKey: List<PokemonRemoteKeys>): Completable

    @Query("Select * From pokemon_remote_key Where pokemon_id = :id")
    fun getRemoteKeyByPokemonID(id: Int): Single<Optional<PokemonRemoteKeys>>

    @Query("Delete From pokemon_remote_key")
    fun clearRemoteKeys() : Completable

    @Query("Select created_at From pokemon_remote_key Order By created_at DESC LIMIT 1")
    fun getCreationTime(): Single<Optional<Long>>

}