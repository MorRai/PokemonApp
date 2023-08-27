package com.example.pokemonapp.ui.pokemon_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.model.Response
import com.example.pokemonapp.adapter.PokemonsRxAdapter
import com.example.pokemonapp.databinding.FragmentListPokemonBinding
import com.example.pokemonapp.util.NetworkConnectivityObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
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
            val adapter = PokemonsRxAdapter(requireContext()) { pokemon ->
               findNavController().navigate(
                   ListPokemonFragmentDirections.actionListPokemonFragmentToDetailPokemonFragment(pokemon.id)
               )
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            // Add a load state listener to the adapter
            adapter.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    isVisibleProgressBar(true)
                } else {
                    isVisibleProgressBar(false)
                    when {
                        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                        loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                        else -> null
                    }?.let {
                        isVisibleProgressBar(false)
                        showErrorToast(it.error)
                    }
                }
            }

            // Observe the view model's view state and update UI accordingly
            mDisposable.add(viewModel.viewState.observeOn(AndroidSchedulers.mainThread()).subscribe { viewState ->
                when (viewState) {
                    is Response.Loading -> isVisibleProgressBar(true)
                    is Response.Success -> {
                        isVisibleProgressBar(false)
                        adapter.submitData(lifecycle, viewState.data)
                    }
                    is Response.Failure -> {
                        isVisibleProgressBar(false)
                        showErrorToast(viewState.e)
                    }
                }
            }
            )
            // Observe network connectivity and trigger data reload on reconnection
            mDisposable.add(
                networkObserver.observeConnectivityStatus()
                    .filter { isConnected -> isConnected }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable {
                        Completable.fromAction {
                            // Retry loading data when network connectivity is restored
                            adapter.retry()
                        }
                    }
                    .subscribe()
            )
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
