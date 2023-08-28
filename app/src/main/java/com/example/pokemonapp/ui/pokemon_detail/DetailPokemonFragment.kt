package com.example.pokemonapp.ui.pokemon_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.domain.model.Pokemon
import com.example.domain.model.Response
import com.example.pokemonapp.Intents.PokemonDetailIntent
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.FragmentDetailPokemonBinding
import com.example.data.util.NetworkConnectivityObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class DetailPokemonFragment : Fragment() {

    private var _binding: FragmentDetailPokemonBinding? = null
    private val binding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    // Disposable container for RxJava disposables
    private val mDisposable = CompositeDisposable()

    // Network connectivity observer instance
    private val networkObserver: NetworkConnectivityObserver by inject()

    // Navigation arguments using SafeArgs
    private val args by navArgs<DetailPokemonFragmentArgs>()

    // ViewModel instance for Pokemon details
    private val viewModel by viewModel<PokemonDetailViewModel> {
        parametersOf(args.id)
    }


    // Inflates the fragment's view and returns the root view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentDetailPokemonBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }


    // Sets up the fragment's views and observes the view model's data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up toolbar with navigation controller
        binding.toolbar.setupWithNavController(findNavController())
        // Subscribe to view model's state changes
        mDisposable.add(viewModel.viewState.subscribe { viewState ->
            when (viewState) {
                is Response.Loading -> isVisibleProgressBar(true)
                is Response.Success -> {
                    isVisibleProgressBar(false)
                    bind(viewState.data)
                }
                is Response.Failure -> {
                    isVisibleProgressBar(false)
                    Toast.makeText(
                        activity, viewState.e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        // Observe network connectivity and trigger reload on reconnection
        mDisposable.add(
            networkObserver.observeConnectivityStatus()
                .filter { isConnected -> isConnected }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable {
                    Completable.fromAction {
                        viewModel.processIntent(PokemonDetailIntent.Load)
                    }
                }
                .subscribe()
        )

        // Load Pokemon details initially
        viewModel.processIntent(PokemonDetailIntent.Load)
    }

    // Binds Pokemon details to the view
    private fun bind(pokemon: Pokemon) {
        with(binding) {
            imageView.load(pokemon.image)
            name.text = pokemon.name
            type.text = pokemon.type
            weight.text = getString(R.string.pokemon_weight, (pokemon.weight / 10).toString())
            height.text = getString(R.string.pokemon_height, (pokemon.height * 10).toString())
        }
    }

    // Toggles the visibility of the progress bar
    private fun isVisibleProgressBar(visible: Boolean) {
        binding.paginationProgressBar.isVisible = visible
    }

    // Clear disposables and view binding instance onDestroyView
    override fun onDestroyView() {
        mDisposable.clear()
        super.onDestroyView()
        _binding = null
    }

    // Dispose of disposables onDestroy
    override fun onDestroy() {
        mDisposable.dispose()
        super.onDestroy()
    }
}
