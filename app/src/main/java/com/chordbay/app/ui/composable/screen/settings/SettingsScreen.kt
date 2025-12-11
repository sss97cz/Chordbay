package com.chordbay.app.ui.composable.screen.settings

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chordbay.app.data.model.chord.HBFormat
import com.chordbay.app.data.model.settings.Settings
import com.chordbay.app.data.model.util.ColorMode
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
    val hbFormat = mainViewModel.hbFormat.collectAsState()
    val colorScheme = mainViewModel.colorMode.collectAsState()
    val themeMode = mainViewModel.themeMode.collectAsState().value


    Scaffold(
        topBar = {
            MyTopAppBar(
                title = "Settings",
                navigationIcon = if (canNavigateBack) Icons.AutoMirrored.Filled.ArrowBack else null,
                onNavigationIconClick = {
                    if (canNavigateBack) {
                        navController.navigateUp()
                    }
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
                    is Settings.ColorModeSetting -> {
                        SettingsRow(
                            settingName = setting.title
                        ) {
                            ColorSchemePicker(
                                selectedColorMode = colorScheme.value,
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