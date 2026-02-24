package com.music.cue.org.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.*
import com.music.cue.org.R
import com.music.cue.org.theme.CueColors
import com.music.cue.org.util.PermissionManager

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    onPermissionsGranted: () -> Unit,
    permissionManager: PermissionManager = PermissionManager(LocalContext.current)
) {
    val context = LocalContext.current
    val permissions = PermissionManager.AUDIO_PERMISSIONS

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = permissions.toList()
    ) { permissionsResult ->
        if (permissionsResult.values.all { it }) {
            onPermissionsGranted()
        }
    }

    // Logic to determine if permissions are permanently denied (no rationale + not granted)
    val isPermanentlyDenied = multiplePermissionsState.permissions.any {
        !it.status.isGranted && !it.status.shouldShowRationale
    } && multiplePermissionsState.shouldShowRationale.not()

    if (multiplePermissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            onPermissionsGranted()
        }
    } else {
        if (multiplePermissionsState.shouldShowRationale) {
            PermissionRationaleScreen(
                permissions = permissions.toList(),
                onRequestPermissions = {
                    multiplePermissionsState.launchMultiplePermissionRequest()
                }
            )
        } else {
            PermissionRequestScreen(
                permissions = permissions.toList(),
                isBlocked = isPermanentlyDenied,
                onRequestPermissions = {
                    if (isPermanentlyDenied) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    } else {
                        multiplePermissionsState.launchMultiplePermissionRequest()
                    }
                }
            )
        }
    }
}

@Composable
fun PermissionRequestScreen(
    permissions: List<String>,
    isBlocked: Boolean,
    onRequestPermissions: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CueColors.BlueDark, CueColors.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(CueColors.BluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Cue Music Player",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = if (isBlocked) "Permissions Required" else "We need a few permissions:",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    permissions.forEach { permission ->
                        PermissionItem(permission)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onRequestPermissions,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CueColors.BluePrimary)
            ) {
                Text(
                    text = if (isBlocked) "Open Settings" else "Grant Permissions",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PermissionRationaleScreen(
    permissions: List<String>,
    onRequestPermissions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CueColors.BlueDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Ensure this exists
            contentDescription = null,
            tint = CueColors.BluePrimary,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Why We Need These Permissions",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(permissions) { permission ->
                    PermissionRationaleItem(permission)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRequestPermissions,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CueColors.BluePrimary)
        ) {
            Text("Continue", color = Color.Black)
        }
    }
}

@Composable
fun PermissionItem(permission: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = getPermissionIconRes(permission)),
            contentDescription = null,
            tint = CueColors.BluePrimary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(getPermissionTitle(permission), color = Color.White, fontWeight = FontWeight.Medium)
            Text(getPermissionDescription(permission), color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}

@Composable
fun PermissionRationaleItem(permission: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = getPermissionIconRes(permission)),
                contentDescription = null,
                tint = CueColors.BluePrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(getPermissionTitle(permission), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Text(
            getPermissionRationale(permission),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 28.dp)
        )
    }
}

// --- Helper Functions ---

private fun getPermissionIconRes(permission: String): Int {
    return when (permission) {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_AUDIO -> R.drawable.ic_launcher_background // Replace with your drawable name
        Manifest.permission.POST_NOTIFICATIONS -> R.drawable.ic_launcher_background
        else -> R.drawable.ic_launcher_background
    }
}

private fun getPermissionTitle(permission: String): String = when (permission) {
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_MEDIA_AUDIO -> "Music Access"
    Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
    else -> "Storage"
}

private fun getPermissionDescription(permission: String): String = when (permission) {
    Manifest.permission.READ_MEDIA_AUDIO -> "Access your music files"
    Manifest.permission.POST_NOTIFICATIONS -> "Show playback controls"
    else -> "Required for app"
}

private fun getPermissionRationale(permission: String): String = when (permission) {
    Manifest.permission.READ_MEDIA_AUDIO -> "This allows Cue to find and play music on your device."
    Manifest.permission.POST_NOTIFICATIONS -> "Notifications let you control playback from your lock screen."
    else -> "This permission is required for the app to function properly."
}