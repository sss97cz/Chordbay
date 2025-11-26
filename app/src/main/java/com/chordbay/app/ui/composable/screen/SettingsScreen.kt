package com.chordbay.app.ui.composable.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chordbay.app.data.model.util.ColorMode
import com.chordbay.app.data.model.util.HBFormat
import com.chordbay.app.data.model.util.Settings
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.data.model.util.ThemeMode
import com.chordbay.app.ui.composable.component.topappbar.MyTopAppBar
import com.chordbay.app.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val canNavigateBack = navController.previousBackStackEntry != null
//    val fontSize = mainViewModel.songTextFontSize.collectAsState().value
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var defaultSortOption by remember { mutableStateOf(SortBy.SONG_NAME) }
    var isFontSizeMenuExpanded by remember { mutableStateOf(false) }
    val hbFormat = mainViewModel.hbFormat.collectAsState()

    val themeMode = mainViewModel.themeMode.collectAsState().value


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
                if (!setting.displayInSettings) continue
                when (setting) {
//                    is Settings.SortBySetting -> {
//                        SettingsRow(
//                            Modifier.fillMaxWidth(),
//                            settingName = setting.title,
//                        ) {
//                            Box(
//                                Modifier
//                                    .clip(CircleShape)
//                                    .background(MaterialTheme.colorScheme.secondaryContainer)
//                            ) {
//                                Row(
//                                    Modifier.clickable(
//                                        onClick = {
//                                            isSortMenuExpanded = true
//                                        }),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        text = defaultSortOption.displayName,
//                                        Modifier.padding(start = 18.dp)
//                                    )
//                                    IconButton(onClick = { isSortMenuExpanded = true }) {
//                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                                    }
//                                    DropdownMenu(
//                                        expanded = isSortMenuExpanded,
//                                        onDismissRequest = { isSortMenuExpanded = false }
//                                    ) {
//                                        for (sortOption in SortBy.entries) {
//                                            DropdownMenuItem(
//                                                text = { Text(sortOption.displayName) },
//                                                onClick = {
//                                                    defaultSortOption = sortOption
//                                                    isSortMenuExpanded = false
//                                                }
//                                            )
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }

                    is Settings.ThemeSetting -> {
                        SettingsRow(settingName = setting.title) {
                            ThemeMode.entries.forEach { mode ->
                                RadioButton(
                                    selected = themeMode == mode,
                                    onClick = { mainViewModel.saveThemeMode(mode) }
                                )
                                Text(
                                    text = mode.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

//                    is Settings.FontSize -> {
//                        SettingsRow(
//                            settingName = setting.title
//                        ) {
//                            Box(contentAlignment = Alignment.BottomEnd) {
//                                Row(
//                                    Modifier
//                                        .clip(CircleShape)
//                                        .background(MaterialTheme.colorScheme.secondaryContainer)
//                                        .clickable { isFontSizeMenuExpanded = true }
//                                        .padding(horizontal = 8.dp, vertical = 4.dp),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    Text(
//                                        text = fontSize.toString(),
//                                        modifier = Modifier.padding(start = 8.dp)
//                                    )
//                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                                }
//
//                                DropdownMenu(
//                                    modifier = Modifier
//                                        .size(40.dp, 300.dp)
//                                        .align(Alignment.BottomEnd),
//                                    expanded = isFontSizeMenuExpanded,
//                                    onDismissRequest = { isFontSizeMenuExpanded = false }
//                                ) {
//                                    HorizontalDivider()
//                                    (10..30 step 2).forEach { sizeOption ->
//                                        DropdownMenuItem(
//                                            text = {
//                                                Box(
//                                                    modifier = Modifier.fillMaxWidth(),
//                                                    contentAlignment = Alignment.Center
//                                                ) {
//                                                    Text(
//                                                        sizeOption.toString(),
//                                                    )
//                                                }
//                                            },
//                                            onClick = {
//                                                mainViewModel.setSongTextFontSize(sizeOption)
//                                                isFontSizeMenuExpanded = false
//                                            }
//                                        )
//                                        HorizontalDivider()
//                                    }
//                                }
//                            }
//                        }
//                    }
                    is Settings.ColorModeSetting -> {
                        SettingsRow(
                            settingName = setting.title
                        ) {
                            ColorSchemePicker(
                                selectedColorMode = mainViewModel.colorMode.collectAsState().value,
                                onColorModeSelected = { selectedMode ->
                                    mainViewModel.saveColorMode(selectedMode)
                                }
                            )
                        }
                    }
                    is Settings.HBFormatSetting -> {
                        SettingsRow(settingName = setting.title) {
                            HBFormat.entries.forEach { format ->
                                RadioButton(
                                    selected = hbFormat.value == format,
                                    onClick = { mainViewModel.saveHBFormat(format) }
                                )
                                Text(
                                    text = format.value,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    else -> {}
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
    OutlinedCard(
        modifier = modifier.padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = settingName)
            Spacer(modifier = Modifier.weight(1f))
            content()
        }
    }
}

@Composable
private fun ColorSchemePicker(
    selectedColorMode: ColorMode,
    onColorModeSelected: (ColorMode) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (colorMode in ColorMode.entries) {
            Card(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onColorModeSelected(colorMode) },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = when (colorMode) {
                        ColorMode.BLUE -> Color.Blue.copy(0.5f)
                        ColorMode.PURPLE -> Color.Magenta.copy(0.5f)
                    }
                ),
                border = if (selectedColorMode == colorMode) {
                    BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    null
                }
            ) {}
        }
    }
}