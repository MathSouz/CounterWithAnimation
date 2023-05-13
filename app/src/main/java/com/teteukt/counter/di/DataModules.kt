package com.teteukt.counter.di

import com.teteukt.counter.data.Repository
import com.teteukt.counter.data.RepositoryImpl
import com.teteukt.counter.data.TimeSchedules
import org.koin.dsl.module

val dataModules = module {
    single { TimeSchedules() }

    factory<Repository> { RepositoryImpl(get()) }
}