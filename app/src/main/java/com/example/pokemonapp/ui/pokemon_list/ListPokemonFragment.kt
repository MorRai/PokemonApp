package com.example.pokemonapp.ui.pokemon_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.model.Response
import com.example.pokemon_app.extension.addPaginationScrollListener
import com.example.pokemonapp.Intents.ListPokemonIntent
import com.example.pokemonapp.adapter.PokemonsAdapter
import com.example.pokemonapp.databinding.FragmentListPokemonBinding
import com.example.data.util.NetworkConnectivityObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListPokemonFragment : Fragment() {

    private var _binding: FragmentListPokemonBinding? = null
    private val binding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    private val viewModel by viewModel<PokemonsListViewModel>()

    private val mDisposable = CompositeDisposable()
    private val networkObserver: NetworkConnectivityObserver by inject()

    // Inflates the fragment's view and returns the root view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return FragmentListPokemonBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    // Sets up the fragment's views and observes the view model's data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            // Create an adapter for displaying Pokemons in a RecyclerView
            val adapter = PokemonsAdapter(requireContext()) { pokemon ->
               findNavController().navigate(
                   ListPokemonFragmentDirections.actionListPokemonFragmentToDetailPokemonFragment(pokemon.id)
               )
            }
            adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())


            recyclerView.addPaginationScrollListener(
                layoutManager = recyclerView.layoutManager as LinearLayoutManager,
                itemsToLoad = 20
            ) {
                viewModel.processIntent(ListPokemonIntent.PageLoad)
            }



            // Observe the view model's view state and update UI accordingly
            mDisposable.add(viewModel.viewState.observeOn(AndroidSchedulers.mainThread()).subscribe { viewState ->
                when (viewState) {
                    is Response.Loading -> isVisibleProgressBar(true)
                    is Response.Success -> {
                        isVisibleProgressBar(false)
                        adapter.submitList(viewState.data)
                        Log.e("page",viewState.data.toString())
                    }
                    is Response.Failure -> {
                        isVisibleProgressBar(false)
                        showErrorToast(viewState.e)
                    }
                    else -> {}
                }
            } )

            // Observe network connectivity and trigger reload on reconnection
            mDisposable.add(viewModel.networkStatusObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // Вызываем нужную логику при доступности сети
                    viewModel.processIntent(ListPokemonIntent.PageLoad)
                })
        }
    }

    // Toggle the visibility of the progress bar
    private fun isVisibleProgressBar(visible: Boolean) {
        binding.paginationProgressBar.isVisible = visible
    }

    // Show an error toast with the provided error message
    private fun showErrorToast(error: Throwable) {
        isVisibleProgressBar(false)
        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {

        mDisposable.clear()
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        mDisposable.dispose()
        super.onDestroy()
    }

}
