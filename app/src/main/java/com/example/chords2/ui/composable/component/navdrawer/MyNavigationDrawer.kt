package com.example.chords2.ui.composable.component.navdrawer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.chords2.data.database.playlist.PlaylistEntity
import com.example.chords2.ui.theme.imagevector.Playlist_add

@Composable
fun MyDrawerContent(
    playlists: List<PlaylistEntity>,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    onManageAccountClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    userName: String? = null,
    userEmail: String? = null
) {
    var playlistsExpanded by remember { mutableStateOf(true) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (playlistsExpanded) -180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    val isSignedIn = !userName.isNullOrBlank() || !userEmail.isNullOrBlank()

    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            // App header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(
                        id = com.example.chords2.R.drawable.ic_launcher_foreground
                    ),
                    contentDescription = null,
                    modifier = Modifier.height(40.dp)
                )
                Text(
                    "My Super App Name",
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Account header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { if (isSignedIn) onManageAccountClick() else onLoginClick() }
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with initials or icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSignedIn) {
                        val initial = (userName?.firstOrNull()
                            ?: userEmail?.firstOrNull()
                            ?: '•').uppercaseChar().toString()
                        Text(
                            initial,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isSignedIn) (userName ?: userEmail ?: "Account")
                        else "You are not signed in",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val subtitle = if (isSignedIn) userEmail ?: "" else "Sign in to sync and back up your data"
                    if (subtitle.isNotBlank()) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isSignedIn) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onManageAccountClick) { Text("Manage") }
                        TextButton(onClick = onSignOutClick) { Text("Sign out") }
                    }
                } else {
                    Button(onClick = onLoginClick) { Text("Sign in") }
                }
            }

            HorizontalDivider()

            // Playlists section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable { playlistsExpanded = !playlistsExpanded }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationDrawerItem(
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    icon = { Icon(Playlist_add, null) },
                    onClick = { playlistsExpanded = !playlistsExpanded },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (playlistsExpanded) {
                playlists.forEachIndexed { i, playlist ->
                    NavigationDrawerItem(
                        label = { Text(playlist.name) },
                        selected = false,
                        onClick = { onPlaylistClick(playlist) },
                    )
                    if (i < playlists.size - 1) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Settings and help
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                onClick = onSettingsClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            NavigationDrawerItem(
                label = { Text("Help and feedback") },
                selected = false,
                icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                onClick = { /* Handle click */ },
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}