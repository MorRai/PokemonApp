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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.adapter.PokemonsRxAdapter
import com.example.pokemonapp.databinding.FragmentListPokemonBinding
import com.example.pokemonapp.util.NetworkConnectivityObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListPokemonFragment : Fragment() {


    private var _binding: FragmentListPokemonBinding? = null
    private val binding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    private val viewModel by viewModel<PokemonsListViewModel>()

    private val mDisposable = CompositeDisposable()
    private lateinit var networkObserver: NetworkConnectivityObserver

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
            val adapter = PokemonsRxAdapter(requireContext()) { pokemon ->
               findNavController().navigate(
                   ListPokemonFragmentDirections.actionListPokemonFragmentToDetailPokemonFragment(pokemon.id)
               )
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())


            mDisposable.add(viewModel.pokemonPagingDataFlow.observeOn(AndroidSchedulers.mainThread()).subscribe {
                adapter.submitData(lifecycle, it)
           }
            )


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
                        Toast.makeText(activity, it.error.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            networkObserver = NetworkConnectivityObserver(requireContext())
            mDisposable.add(
                networkObserver.observeConnectivityStatus()
                    .filter { isConnected -> isConnected }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable {
                        Completable.fromAction {
                            Log.e("page", "ыефкпкеркеркерк")
                            adapter.retry()
                        }
                    }
                    .subscribe()
            )


        }
    }


    private fun isVisibleProgressBar(visible: Boolean) {
        binding.paginationProgressBar.isVisible = visible
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
