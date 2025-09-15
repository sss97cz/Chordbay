package com.example.chords2.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chords2.data.model.util.Settings
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel

@Composable
fun SettingsScreen(
    songViewModel: SongViewModel,
    navController: NavController
) {
    val canNavigateBack = navController.previousBackStackEntry != null
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
                SettingsRow(
                    Modifier.fillMaxWidth(),
                    settingName = setting.title,
                    content = setting.content
                )
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
        modifier = modifier.fillMaxWidth(),
    ) {

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = settingName)
            Spacer(modifier = Modifier.weight(1f))
            content()
        }
        HorizontalDivider()
    }
}







