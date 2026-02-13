package com.music.cue.org.ui.theme

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
                text = "Explorer",
                fontSize = 20.sp,
                color = colorResource(R.color.white),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 44.dp, bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            if (!permissionState.status.isGranted) {
                Button(
                    onClick = {permissionState.launchPermissionRequest()},
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Permissions required"
                    )
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



