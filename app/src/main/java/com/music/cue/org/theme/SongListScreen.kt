package com.music.cue.org.theme

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.music.cue.org.R
import com.music.cue.org.data.Song
import com.music.cue.org.data.getSongs

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SongListScreen(
    onSongClick: (song: List<Song>, position: Int)-> Unit
){
    val context = LocalContext.current
    val songState = remember{mutableStateOf<List<Song>>(emptyList())}
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionState = rememberPermissionState(permission)
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
          songState.value = getSongs(context)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        ){
        Image(
            painter = painterResource(R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            Modifier.fillMaxSize()
        ) {
            Text(
                text = "My Music Library",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(top = 60.dp, bottom = 24.dp, start = 20.dp)
            )

            if (!permissionState.status.isGranted) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "We need access to your music",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { permissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Grant Permission", fontWeight = FontWeight.Bold)
                    }
                }
            }
            SongList(
                songs = songState.value,
                onSongClick = {position ->
                    onSongClick(songState.value, position)
                },
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}



