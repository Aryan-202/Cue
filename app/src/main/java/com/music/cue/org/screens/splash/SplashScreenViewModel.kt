package com.music.cue.org.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel: ViewModel() {
    private val _isSplashScreenVisible = MutableStateFlow(true)
    val isSplashScreenVisible = _isSplashScreenVisible.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000)
            _isSplashScreenVisible.value = false
        }
    }
}