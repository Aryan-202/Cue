package com.music.cue.org.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        // Audio permissions based on Android version
        val AUDIO_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // All permissions needed for the app
        val ALL_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.FOREGROUND_SERVICE
            )
        }

        // Permission request codes
        const val REQUEST_CODE_AUDIO = 1001
        const val REQUEST_CODE_NOTIFICATION = 1002
        const val REQUEST_CODE_ALL = 1003
    }

    // LiveData for observing permission states
    private val _audioPermissionGranted = MutableLiveData<Boolean>()
    val audioPermissionGranted: LiveData<Boolean> = _audioPermissionGranted

    private val _notificationPermissionGranted = MutableLiveData<Boolean>()
    val notificationPermissionGranted: LiveData<Boolean> = _notificationPermissionGranted

    private val _allPermissionsGranted = MutableLiveData<Boolean>()
    val allPermissionsGranted: LiveData<Boolean> = _allPermissionsGranted

    private val _deniedPermissions = MutableLiveData<List<String>>()
    val deniedPermissions: LiveData<List<String>> = _deniedPermissions

    private val _shouldShowRationale = MutableLiveData<Map<String, Boolean>>()
    val shouldShowRationale: LiveData<Map<String, Boolean>> = _shouldShowRationale

    init {
        checkPermissionStates()
    }

    /**
     * Check current permission states
     */
    fun checkPermissionStates() {
        val audioGranted = hasAudioPermissions()
        val notificationGranted = hasNotificationPermission()

        _audioPermissionGranted.postValue(audioGranted)
        _notificationPermissionGranted.postValue(notificationGranted)

        val allGranted = audioGranted && notificationGranted
        _allPermissionsGranted.postValue(allGranted)

        // Get list of denied permissions
        val denied = ALL_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
        _deniedPermissions.postValue(denied)
    }

    /**
     * Check if audio permissions are granted
     */
    fun hasAudioPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notification permission not required before Android 13
        }
    }

    /**
     * Check if a specific permission is granted
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if multiple permissions are granted
     */
    fun arePermissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get permission description for UI
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_AUDIO -> "Access your music files to play songs"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Save playlists and offline music"
            Manifest.permission.POST_NOTIFICATIONS -> "Show music controls in notifications"
            Manifest.permission.FOREGROUND_SERVICE -> "Play music in the background"
            else -> "Required for app functionality"
        }
    }

    /**
     * Get permission title for UI
     */
    fun getPermissionTitle(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_AUDIO -> "Music Access"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Storage Access"
            Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
            Manifest.permission.FOREGROUND_SERVICE -> "Background Play"
            else -> "Permission Required"
        }
    }

    /**
     * Get icon resource for permission (you'll need to add these drawables)
     */
    fun getPermissionIcon(permission: String): Int {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_AUDIO -> android.R.drawable.ic_menu_gallery
            Manifest.permission.POST_NOTIFICATIONS -> android.R.drawable.ic_dialog_info
            Manifest.permission.FOREGROUND_SERVICE -> android.R.drawable.ic_media_play
            else -> android.R.drawable.ic_dialog_alert
        }
    }

    /**
     * Handle permission result
     */
    fun onPermissionResult(
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val deniedPermissionsList = mutableListOf<String>()
        val rationaleMap = mutableMapOf<String, Boolean>()

        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionsList.add(permission)
                // Note: We can't check shouldShowRequestPermissionRationale here as we don't have an Activity reference
                // This should be handled in the UI layer
            }
        }

        _deniedPermissions.postValue(deniedPermissionsList)
        checkPermissionStates()
    }

    /**
     * Get rationale message for permission
     */
    fun getPermissionRationale(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_AUDIO ->
                "Cue needs access to your music files to play your favorite songs. " +
                        "Without this permission, you won't be able to play any music."

            Manifest.permission.POST_NOTIFICATIONS ->
                "Cue uses notifications to show you what's playing and provide easy playback controls. " +
                        "This helps you control music without opening the app."

            Manifest.permission.FOREGROUND_SERVICE ->
                "Cue needs to run in the background to keep playing music when you switch to other apps. " +
                        "This allows you to listen while using other apps or with your screen off."

            else -> "This permission is required for the app to function properly."
        }
    }
}