package com.workflow.expense.presentation.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseMviViewModel<Intent, State, Effect>(initialState: State) : ViewModel() {
    protected val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state

    protected val _effect: MutableSharedFlow<Effect> = MutableSharedFlow(extraBufferCapacity = 1)
    val effect: SharedFlow<Effect> = _effect

    abstract fun dispatch(intent: Intent)
}


