package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.model.util.ThemeMode
import com.example.chords2.ui.composable.navigation.Paths
import com.example.chords2.ui.composable.component.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel

@Composable
fun SettingsScreen(
    songViewModel: SongViewModel,
    navController: NavController
) {
    val canNavigateBack = navController.previousBackStackEntry != null
    val fontSize = songViewModel.songTextFontSize.collectAsState().value
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var defaultSortOption by remember { mutableStateOf(SortBy.SONG_NAME) }
    var isFontSizeMenuExpanded by remember { mutableStateOf(false) }
    val themeMode = songViewModel.themeMode.collectAsState().value


    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Settings",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                onNavigationIconClick = {
                    navController.navigate(Paths.HomePath.route)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (setting in Settings.all) {
                when (setting) {
                    is Settings.SortBySetting -> {
                        SettingsRow(
                            Modifier.fillMaxWidth(),
                            settingName = setting.title,
                        ) {
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Row(
                                    Modifier.clickable(
                                        onClick = {
                                            isSortMenuExpanded = true
                                        }),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = defaultSortOption.displayName,
                                        Modifier.padding(start = 8.dp)
                                    )
                                    IconButton(onClick = { isSortMenuExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                    DropdownMenu(
                                        expanded = isSortMenuExpanded,
                                        onDismissRequest = { isSortMenuExpanded = false }
                                    ) {
                                        for (sortOption in SortBy.entries) {
                                            DropdownMenuItem(
                                                text = { Text(sortOption.displayName) },
                                                onClick = {
                                                    defaultSortOption = sortOption
                                                    isSortMenuExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is Settings.ThemeSetting -> {
                        SettingsRow(settingName = setting.title) {
                            ThemeMode.entries.forEach { mode ->
                                RadioButton(
                                    selected = themeMode == mode,
                                    onClick = { songViewModel.saveThemeMode(mode) }
                                )
                                Text(
                                    text = mode.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    is Settings.FontSize -> {
                        SettingsRow(
                            settingName = setting.title
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Row(
                                    Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .clickable { isFontSizeMenuExpanded = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fontSize.toString(),
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }

                                DropdownMenu(
                                    modifier = Modifier
                                        .size(40.dp, 300.dp)
                                        .align(Alignment.BottomEnd),
                                    expanded = isFontSizeMenuExpanded,
                                    onDismissRequest = { isFontSizeMenuExpanded = false }
                                ) {
                                    HorizontalDivider()
                                    (10..30 step 2).forEach { sizeOption ->
                                        DropdownMenuItem(
                                            text = {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        sizeOption.toString(),
                                                    )
                                                }
                                            },
                                            onClick = {
                                                songViewModel.setSongTextFontSize(sizeOption)
                                                isFontSizeMenuExpanded = false
                                            }
                                        )
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    modifier: Modifier = Modifier,
    settingName: String,
    content: @Composable () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = settingName)
            Spacer(modifier = Modifier.weight(1f))
            content()
            Spacer(Modifier.padding(bottom = 2.dp))
        }
    }
    HorizontalDivider()
}