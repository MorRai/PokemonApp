package com.example.pokemonapp.ui.pokemon_list

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.data.util.NetworkConnectivityObserver
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.domain.usecase.GetPokemonsFromCache
import com.example.domain.usecase.LoadPokemonUseCase
import com.example.pokemonapp.Intents.ListPokemonIntent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


class PokemonsListViewModel(
    private val loadPokemonUseCase: LoadPokemonUseCase,
    private val getPokemonsFromCache: GetPokemonsFromCache,
    private val networkObserver: NetworkConnectivityObserver,
) : ViewModel() {

    private val mDisposable = CompositeDisposable()


    private val networkStatusSubject = PublishSubject.create<Unit>()
    val networkStatusObservable: Observable<Unit> = networkStatusSubject

    private val _viewState = BehaviorSubject.create<Response<List<Pokemon>>>()

    // Exposed observable for observing view state changes.
    val viewState: Observable<Response<List<Pokemon>>> = _viewState.hide()

    private val cachedPokemons: MutableList<Pokemon> = mutableListOf()
    private var currentPage = 1
    private var isPageLoading = false // Флаг для отслеживания состояния загрузки

    init {
        processIntent(ListPokemonIntent.InitialLoad)
        mDisposable.add(networkObserver.observeConnectivityStatus()
            .filter { isConnected -> isConnected }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                networkStatusSubject.onNext(Unit)
            }

        )
    }

    fun processIntent(intent: ListPokemonIntent) {
        when (intent) {
            is ListPokemonIntent.InitialLoad -> loadCachedPokemons()
            is ListPokemonIntent.PageLoad -> refreshCache()
        }
    }


    @SuppressLint("CheckResult")
    private fun loadCachedPokemons() {
        _viewState.onNext(Response.Loading)
        getPokemonsFromCache.invoke().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ cachedPokemonsResponse ->
                if (cachedPokemonsResponse is Response.Success && cachedPokemonsResponse.data.isNotEmpty()) {
                    _viewState.onNext(cachedPokemonsResponse)
                    this.cachedPokemons.clear()
                    this.cachedPokemons.addAll(cachedPokemonsResponse.data)
                } else if (cachedPokemonsResponse is Response.Failure) {
                    _viewState.onNext(Response.Failure(cachedPokemonsResponse.e))
                }
                refreshCache()
            }, { error ->
                _viewState.onNext(Response.Failure(error))
                refreshCache()
            })
    }

    @SuppressLint("CheckResult")
    private fun refreshCache() {
        if (!networkObserver.isConnected() || isPageLoading) {
            // Пропускаем повторный вызов, если загрузка уже идет или нет интернета
            return
        }
        _viewState.onNext(Response.Loading)
        Log.e("page", "load+$currentPage")
        isPageLoading = true // Устанавливаем флаг в true перед началом загрузки
        loadPokemonUseCase.invoke(currentPage).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                if (response is Response.Success) {
                    val newPokemons = response.data
                    Log.e("page", newPokemons.toString())
                    // Обновляем кэшированные данные, если id совпадает
                    newPokemons.forEach { newPokemon ->
                        val existingPokemonIndex =
                            cachedPokemons.indexOfFirst { it.id == newPokemon.id }
                        if (existingPokemonIndex >= 0) {
                            cachedPokemons[existingPokemonIndex] = newPokemon
                        } else {
                            cachedPokemons.add(newPokemon)
                        }
                    }
                    _viewState.onNext(Response.Success(cachedPokemons))

                    currentPage++
                } else if (response is Response.Failure) {
                    _viewState.onNext(Response.Failure(response.e))
                }
                isPageLoading = false
            }, { error ->
                _viewState.onNext(Response.Failure(error))
                isPageLoading = false
            })
    }

    override fun onCleared() {
        mDisposable.clear()
        super.onCleared()
    }

}