package com.example.pokemonapp.ui.pokemon_detail

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.usecase.GetPokemonDetailUseCase
import com.example.pokemonapp.Intents.PokemonDetailIntent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class PokemonDetailViewModel(
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase,
    private val pokemonId: Int,
) : ViewModel() {
    // BehaviorSubject to hold the current state of the view.
    private val _viewState = BehaviorSubject.create<Response<Pokemon>>()

    // Exposed observable for observing view state changes.
    val viewState: Observable<Response<Pokemon>> = _viewState.hide()

    fun processIntent(intent: PokemonDetailIntent) {
        when (intent) {
            is PokemonDetailIntent.Load -> loadPokemonDetail()
        }
    }

    //Processes incoming intents and triggers appropriate actions.
    @SuppressLint("CheckResult")
    private fun loadPokemonDetail() {
        _viewState.onNext(Response.Loading)
        // Fetch Pokemon details using the use case.
        getPokemonDetailUseCase(pokemonId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pokemon ->
                _viewState.onNext(pokemon)
            }, { error ->
                _viewState.onNext(Response.Failure(error))
            })
    }
}



