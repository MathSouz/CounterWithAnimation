package com.teteukt.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    fun launch(errorBlock: (() -> Unit)? = null, block: suspend () -> Unit) {
        try {
            viewModelScope.launch {
                block.invoke()
            }
        } catch (ex: Exception) {
            errorBlock?.invoke()
        }
    }
}