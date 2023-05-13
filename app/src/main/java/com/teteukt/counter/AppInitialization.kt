package com.teteukt.counter

import android.app.Application
import com.teteukt.counter.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppInitialization : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppInitialization)
            androidLogger()
            modules(appModules)
        }
    }
}