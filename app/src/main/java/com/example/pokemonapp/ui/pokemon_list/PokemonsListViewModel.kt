package com.example.pokemonapp.ui.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava3.cachedIn
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.usecase.GetPokemonsUseCase
import com.example.pokemonapp.Intents.ListPokemonIntent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


class PokemonsListViewModel(private val getPokemonsUseCase: GetPokemonsUseCase) : ViewModel() {

    private val _viewState = BehaviorSubject.create<Response<PagingData<Pokemon>>>()
    // Exposed observable for observing view state changes.
    val viewState: Observable<Response<PagingData<Pokemon>>> = _viewState.hide()


    init {
        processIntent(ListPokemonIntent.InitialLoad)
    }
    //как то не особо mvi, но какой-то интерактивности от пользователя нет, ибо загрузка сразу по приходу на экран
    fun processIntent(intent: ListPokemonIntent) {
        when (intent) {
            is ListPokemonIntent.InitialLoad -> loadPokemons()
        }
    }

    // Load the list of Pokemons and update the view state accordingly.
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadPokemons() {
        _viewState.onNext(Response.Loading)
        viewModelScope.launch {
        getPokemonsUseCase.invoke().cachedIn(viewModelScope)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ pokemon ->
                _viewState.onNext(Response.Success(pokemon))
            }, { error ->
                _viewState.onNext(Response.Failure(error))
            })

        }
    }

}