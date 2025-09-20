package com.example.chords2.ui.composable.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chords2.ui.composable.screen.EditSongScreen
import com.example.chords2.ui.composable.screen.HomeScreen
import com.example.chords2.ui.composable.screen.SettingsScreen
import com.example.chords2.ui.composable.screen.SongScreen
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    // modifier: Modifier = Modifier,
    viewModel: SongViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Paths.HomePath.route
    ) {
        composable(
            route = Paths.HomePath.route
        ) {
            HomeScreen(
               // modifier = Modifier.padding(innerPadding),
                songViewModel = viewModel,
                navController = navController,
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
                    songViewModel = viewModel,
                    navController = navController,
                    songId = songId,
                )
            } else {
                navController.popBackStack()
            }
        }
        composable(
            route = Paths.PostPath.route,
            arguments = listOf(
                navArgument(
                    name = "postId"
                ) {
                    type = NavType.StringType
                }
            )
        ) {
            val postId = it.arguments?.getString("postId")
            if (postId != null) {
                Log.d("AppNavigation", "postId: $postId")
                SongScreen(
                    songViewModel = viewModel,
                    navController = navController,
                    songId = postId,
                    isRemote = true
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(
            route = Paths.SettingsPath.route
        ) {
            SettingsScreen(
                songViewModel = viewModel,
                navController = navController
            )
        }
    }
}
