package com.example.chords2.ui.composable.screen

import android.inputmethodservice.Keyboard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel

@Composable
fun SettingsScreen(
    songViewModel: SongViewModel,
    navController: NavController
) {
    val canNavigateBack = navController.previousBackStackEntry != null
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var defoultSortOption by remember { mutableStateOf(SortBy.SONG_NAME) }
    var isDarkTheme by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Settings",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                onNavigationIconClick = {
                    navController.popBackStack()
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
                        ){
                            Box(Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer)){
                                Row(
                                    Modifier,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = defoultSortOption.displayName, Modifier.padding(start = 2.dp))
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
                                                    defoultSortOption = sortOption
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Settings.ThemeSetting -> {
                        SettingsRow(
                            settingName = setting.title,
                        ){
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = {
                                    isDarkTheme = it
                                }
                            )
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
        Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp),
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







