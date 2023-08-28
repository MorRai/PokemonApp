package com.example.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkConnectivityObserver(context: Context) {
    // ConnectivityManager instance to manage network connectivity.
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // MutableStateFlow to emit connectivity status changes.
    private val connectivityStatusFlow = MutableStateFlow(isConnected())

    // Flow to observe connectivity status changes.
    fun observeConnectivityStatus() = connectivityStatusFlow.asStateFlow()

    // Checks if the device is currently connected to the internet.
    private fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    init {
        // Create a NetworkRequest to listen for network changes.
        val networkRequest = NetworkRequest.Builder().build()
        // Callback for network availability changes.
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Emit a true value indicating network availability.
                connectivityStatusFlow.value = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Emit a false value indicating network unavailability.
                connectivityStatusFlow.value = false
            }
        }
        // Register the network callback to listen for changes.
        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }
}