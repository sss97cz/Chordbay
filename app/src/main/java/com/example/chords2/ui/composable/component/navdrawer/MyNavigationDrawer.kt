package com.example.chords2.ui.composable.component.navdrawer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.chords2.data.database.playlist.PlaylistEntity
import com.example.chords2.ui.theme.imagevector.Playlist_add

@Composable
fun MyDrawerContent(
    playlists: List<PlaylistEntity>,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onSettingsClick: () -> Unit
) {
    var playlistsExpanded by remember { mutableStateOf(true) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (playlistsExpanded) -180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.chords2.R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .height(40.dp)
                )
                Text(
                    "My Super App Name",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            HorizontalDivider()

            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { playlistsExpanded = !playlistsExpanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Text(
//                    "Playlists",
//                    modifier = Modifier
//                        .padding(16.dp),
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Spacer(Modifier.weight(1f))
//                Icon(Icons.Default.ArrowDropDown, null,
//                    modifier = Modifier
//                        .padding(end = 8.dp)
//                        .rotate(if (playlistsExpanded) 180f else 0f)
//                )
                NavigationDrawerItem(
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Playlists", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.weight(1f))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.rotate(animatedRotation)
                            )
                        }
                    },
                    selected = false,
                    icon = {
                        Icon(Playlist_add, null)
                    },
                    onClick = { playlistsExpanded = !playlistsExpanded },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
            }
            if (playlistsExpanded) {
                playlists.forEachIndexed { i, playlist ->
                    NavigationDrawerItem(
                        label = { Text(playlist.name) },
                        selected = false,
                        onClick = {
                            onPlaylistClick(playlist)
                        },
                    )
                    if (i < playlists.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = onSettingsClick,
            )
            NavigationDrawerItem(
                label = { Text("Help and feedback") },
                selected = false,
                icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                onClick = { /* Handle click */ },
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}