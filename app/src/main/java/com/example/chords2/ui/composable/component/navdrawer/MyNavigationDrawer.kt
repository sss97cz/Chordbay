package com.example.chords2.ui.composable.component.navdrawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chords2.data.database.playlist.PlaylistEntity
import com.example.chords2.ui.composable.component.list.AlphabeticalSongList
import com.example.chords2.ui.theme.imagevector.Playlist_add

@Composable
fun MyDrawerContent(
    playlists: List<PlaylistEntity>,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    onManageAccountClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    userEmail: String? = null
) {
    var playlistsExpanded by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (playlistsExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 150)
    )
    val isSignedIn = !userEmail.isNullOrBlank()

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Header
            item {
                DrawerHeader()
                HorizontalDivider(thickness = 1.dp)
            }

            // Account Section
            item {
                SectionHeader("Account")
                AccountRow(
                    isSignedIn = isSignedIn,
                    userEmail = userEmail,
                    onLoginClick = onLoginClick,
                    onManageAccountClick = onManageAccountClick,
                    onSignOutClick = onSignOutClick
                )
                HorizontalDivider(Modifier.padding(top = 8.dp))
            }

            // Playlists Section
            item {
                SectionHeader("Library")
                NavigationDrawerItem(
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
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
                    icon = { Icon(Icons.Default.LibraryMusic, null) },
                    onClick = { playlistsExpanded = !playlistsExpanded },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                AnimatedVisibility(visible = playlistsExpanded) {
                    Column {
                        playlists.forEachIndexed { i, playlist ->
                            NavigationDrawerItem(
                                label = { Text(playlist.name) },
                                selected = false,
                                onClick = { onPlaylistClick(playlist) },
                                modifier = Modifier
                                    .padding(horizontal = 32.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )
                            if (i < playlists.size - 1) HorizontalDivider()
                        }
                    }
                }

                HorizontalDivider()
            }

            // Settings Section
            item {
                SectionHeader("Settings")
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    onClick = onSettingsClick,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Help & feedback") },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                    onClick = { /* TODO: open help */ },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
@Composable
private fun DrawerHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = com.example.chords2.R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.height(40.dp)
        )
        Text(
            "Chordbay",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
fun AccountRow(
    isSignedIn: Boolean,
    userEmail: String? = null,
    onLoginClick: () -> Unit = {},
    onManageAccountClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSignedIn) {
                        Text(
                            (userEmail?.firstOrNull() ?: '•').uppercaseChar().toString(),
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
                Column {
                    Text(
                        if (isSignedIn) userEmail ?: "Account" else "You are not signed in",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (!isSignedIn) {
                        TextButton(onClick = onLoginClick) {
                            Icon(Icons.Outlined.Login, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Sign in")
                        }
                    }
                }
            }

            if (isSignedIn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = onManageAccountClick) { Text("Manage") }
                    TextButton(onClick = onSignOutClick) { Text("Sign out") }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun AccountRowPreview() {
    AccountRow(
        isSignedIn = true,
        userEmail = "user001@chordbay.com"
    )
}