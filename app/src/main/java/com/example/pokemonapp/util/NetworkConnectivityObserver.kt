package com.example.pokemonapp.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class NetworkConnectivityObserver(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val connectivityStatusSubject = BehaviorSubject.createDefault(isConnected())

    fun observeConnectivityStatus(): Observable<Boolean> {
        return connectivityStatusSubject.distinctUntilChanged()
    }

    private fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    init {
        val networkRequest = NetworkRequest.Builder().build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityStatusSubject.onNext(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                connectivityStatusSubject.onNext(false)
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }
}