package com.example.data.di

import com.example.data.util.NetworkConnectivityObserver
import org.koin.dsl.module

internal val networkCheckModule = module {
    single { NetworkConnectivityObserver(get()) }
}