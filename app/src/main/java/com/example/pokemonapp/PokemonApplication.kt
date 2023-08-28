package com.example.pokemonapp

import android.app.Application
import com.example.data.di.dataModule
import com.example.pokemonapp.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class PokemonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@PokemonApplication)
            modules(
                dataModule,
                viewModelsModule,
            )
        }
    }

}