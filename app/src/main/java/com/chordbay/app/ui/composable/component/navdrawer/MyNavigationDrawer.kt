package com.chordbay.app.ui.composable.component.navdrawer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chordbay.app.R
import com.chordbay.app.data.database.playlist.PlaylistEntity
import com.chordbay.app.data.helper.pluralText
import com.chordbay.app.data.model.PlaylistInfo

@Composable
fun MyDrawerContent(
    playlists: List<PlaylistInfo>,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit = {},
    onManageAccountClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    onHelpAndFeedbackClick: () -> Unit = {},
    onLegalClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    userEmail: String? = null,
) {
    var playlistsExpanded by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (playlistsExpanded) 0f else 90f,
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
                PlaylistSection(
                    playlists = playlists,
                    playlistsExpanded = playlistsExpanded,
                    onToggleExpand = { playlistsExpanded = !playlistsExpanded },
                    onPlaylistClick = onPlaylistClick,
                    onCreatePlaylistClick = onCreatePlaylistClick,
                    animatedRotation = animatedRotation
                )
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
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Help") },
                    selected = false,
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = null
                        )
                    },
                    onClick = onHelpAndFeedbackClick,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Legal") },
                    selected = false,
                    icon = { Icon(Icons.Default.Description, null) },
                    onClick = onLegalClick,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                NavigationDrawerItem(
                    label = { Text("About & Support") },
                    selected = false,
                    icon = { Icon(Icons.Default.Info, null) },
                    onClick = onAboutClick,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
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
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )

        Text(
            "Chordbay",
            modifier = Modifier.padding(start = 12.dp),
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
                            Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = null)
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
private fun PlaylistSection(
    playlists: List<PlaylistInfo>,
    playlistsExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    animatedRotation: Float
) {
    val playlistListState = rememberLazyListState()

    SectionHeader("Library")
    NavigationDrawerItem(
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Playlists")
                Spacer(Modifier.weight(1f))
                if (playlists.isNotEmpty()) {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(animatedRotation)
                    )
                }
            }
        },
        selected = false,
        icon = { Icon(Icons.Default.LibraryMusic, null) },
        onClick = {
            if (playlists.isNotEmpty()) {
                onToggleExpand()
            } else {
                onCreatePlaylistClick()
            }
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
    AnimatedVisibility(visible = playlistsExpanded) {
        LazyColumn(
            state = playlistListState,
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            items(playlists) { playlistInfo ->
                PlaylistItemCard(
                    playlistInfo = playlistInfo,
                    onClick = { onPlaylistClick(playlistInfo.playlist) }
                )
            }
        }
    }
    HorizontalDivider(Modifier.padding(top = 4.dp))
}

@Composable
private fun PlaylistItemCard(
    playlistInfo: PlaylistInfo,
    onClick: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .padding(start = 56.dp, end = 16.dp, top = 6.dp, bottom = 2.dp)
            .fillMaxWidth()
            .animateContentSize()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Thumbnail / icon container
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryMusic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playlistInfo.playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )

                // Example secondary text; adapt to your `PlaylistEntity`
                Text(
                    text = if (playlistInfo.songCount != 0) {
                        pluralText("${playlistInfo.songCount} song", playlistInfo.songCount)
                    } else {
                        "No songs"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
