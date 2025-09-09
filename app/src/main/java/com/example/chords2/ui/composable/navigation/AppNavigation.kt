package com.example.chords2.ui.composable.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chords2.ui.composable.screen.EditSongScreen
import com.example.chords2.ui.composable.screen.HomeScreen
import com.example.chords2.ui.composable.screen.SongScreen
import com.example.chords2.ui.composable.topappbar.MyTopAppBar
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
   // modifier: Modifier = Modifier,
    viewModel: SongViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val canNavigateBack = navController.previousBackStackEntry != null
    val screenTitle = when (currentDestination) {
        Paths.HomePath.route -> "Home"
        Paths.SongPath.route -> "Song"
        Paths.EditSongPath.route -> "Song Editor"
        else -> ""
    }
    Scaffold(
        topBar = {
            MyTopAppBar(
                title = screenTitle,
                navigationIcon = if (canNavigateBack) {
                    Icons.AutoMirrored.Filled.ArrowBack
                } else {
                    null
                },
                onNavigationIconClick = if (canNavigateBack) {
                    { navController.navigateUp() }
                } else {
                    {}
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Paths.HomePath.route
        ) {
            composable(
                route = Paths.HomePath.route
            ) {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    songViewModel = viewModel,
                    navController = navController
                )
            }

            composable(
                route = Paths.SongPath.route,
                arguments = listOf(
                    navArgument(
                        name = "songId"
                    ) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val songId = backStackEntry.arguments?.getString("songId")
                if (songId != null) {
                    SongScreen(
                        modifier = Modifier.padding(innerPadding),
                        songViewModel = viewModel,
                        navController = navController,
                        songId = songId
                    )
                } else {
                    navController.popBackStack()
                }
            }

            composable(
                route = Paths.EditSongPath.route,
                arguments = listOf(
                    navArgument(
                        name = "songId"
                    ) {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) {
                val songId = it.arguments?.getString("songId")
                if (songId != null) {
                    EditSongScreen(
                        modifier = Modifier.padding(innerPadding),
                        songViewModel = viewModel,
                        navController = navController,
                        songId = songId
                    )
                } else {
                    navController.popBackStack()
                }
            }
        }
    }
}