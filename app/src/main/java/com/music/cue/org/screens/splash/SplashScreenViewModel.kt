package com.music.cue.org.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel: ViewModel() {
    private val _isSplashScreenVisible = MutableStateFlow(true)
    val isSplashScreenVisible = _isSplashScreenVisible.asStateFlow()

    init {
        viewModelScope.launch {
            // Remove the delay so the system splash screen hands over to the custom animation immediately
            _isSplashScreenVisible.value = false
        }
    }
}