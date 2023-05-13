package com.teteukt.counter.di

import com.teteukt.counter.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModules = module {
    viewModel { MainViewModel(get()) }
}