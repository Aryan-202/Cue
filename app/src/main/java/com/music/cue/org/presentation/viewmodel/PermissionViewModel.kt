package com.music.cue.org.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.cue.org.util.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionManager: PermissionManager
) : ViewModel() {

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.Initial)
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    private val _deniedPermissions = MutableStateFlow<List<String>>(emptyList())
    val deniedPermissions: StateFlow<List<String>> = _deniedPermissions.asStateFlow()

    init {
        checkPermissions()

        // Observe permission changes
        viewModelScope.launch {
            permissionManager.allPermissionsGranted.observeForever { granted ->
                if (granted == true) {
                    _permissionState.value = PermissionState.Granted
                }
            }
        }

        viewModelScope.launch {
            permissionManager.deniedPermissions.observeForever { denied ->
                _deniedPermissions.value = denied ?: emptyList()
                if (denied?.isNotEmpty() == true) {
                    _permissionState.value = PermissionState.Denied(denied)
                }
            }
        }
    }

    fun checkPermissions() {
        permissionManager.checkPermissionStates()
        val hasAudio = permissionManager.hasAudioPermissions()
        val hasNotification = permissionManager.hasNotificationPermission()

        _permissionState.value = if (hasAudio && hasNotification) {
            PermissionState.Granted
        } else {
            PermissionState.NotGranted
        }
    }

    fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        permissionManager.onPermissionResult(permissions, grantResults)
    }

    fun resetState() {
        _permissionState.value = PermissionState.Initial
    }

    sealed class PermissionState {
        object Initial : PermissionState()
        object NotGranted : PermissionState()
        object Granted : PermissionState()
        data class Denied(val permissions: List<String>) : PermissionState()
        data class PermanentlyDenied(val permissions: List<String>) : PermissionState()
    }
}