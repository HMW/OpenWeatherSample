package com.hm.core.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Event> : ViewModel() {
    
    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    
    protected fun setState(newState: State) {
        _uiState.value = newState
    }
    
    protected fun updateState(update: State.() -> State) {
        _uiState.value = _uiState.value.update()
    }
    
    protected fun launch(block: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                block()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
    
    abstract fun createInitialState(): State
    abstract fun handleEvent(event: Event)
    abstract fun handleError(error: Exception)
}
