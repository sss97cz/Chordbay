package com.example.chords2.ui.composable.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chords2.ui.composable.screen.ArtistSongsScreen
import com.example.chords2.ui.composable.screen.EditSongScreen
import com.example.chords2.ui.composable.screen.HomeScreen
import com.example.chords2.ui.composable.screen.PlaylistScreen
import com.example.chords2.ui.composable.screen.SettingsScreen
import com.example.chords2.ui.composable.screen.SongScreen
import com.example.chords2.ui.composable.screen.user.LoginScreen
import com.example.chords2.ui.composable.screen.user.ManageAccountScreen
import com.example.chords2.ui.composable.screen.user.RegisterScreen
import com.example.chords2.ui.composable.screen.user.VerifyEmailScreen
import com.example.chords2.ui.viewmodel.AuthViewModel
import com.example.chords2.ui.viewmodel.EditViewModel
import com.example.chords2.ui.viewmodel.RemoteSongsViewModel
import com.example.chords2.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    // modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    remoteSongsViewModel: RemoteSongsViewModel = koinViewModel(),
    editViewModel: EditViewModel = koinViewModel(),
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
                mainViewModel = mainViewModel,
                authViewModel = authViewModel,
                navController = navController,
                remoteSongsViewModel = remoteSongsViewModel,
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
                    mainViewModel = mainViewModel,
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
                    viewModel = editViewModel,
                    navController = navController,
                    songId = songId,
                )
            } else {
                navController.popBackStack()
            }
        }
        composable(
            route = Paths.RemoteSongPath.route,
            arguments = listOf(
                navArgument(
                    name = "remoteId"
                ) {
                    type = NavType.StringType
                }
            )
        ) {
            val remoteId = it.arguments?.getString("remoteId")
            if (remoteId != null) {
                Log.d("AppNavigation", "remoteId: $remoteId")
                SongScreen(
                    mainViewModel = mainViewModel,
                    navController = navController,
                    songId = remoteId,
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
                mainViewModel = mainViewModel,
                navController = navController
            )
        }

        composable(
            route = Paths.PlaylistPath.route,
            arguments = listOf(
                navArgument(
                    name = "playlistId"
                ) {
                    type = NavType.IntType
                }
            )
        ) {
            val playlistId = it.arguments?.getInt("playlistId")
            if (playlistId != null) {
                PlaylistScreen(
                    // modifier = Modifier.padding(innerPadding),
                    mainViewModel = mainViewModel,
                    navController = navController,
                    playlistId = playlistId
                )
            } else {
                navController.popBackStack()
            }
        }

        composable(
            route = Paths.ArtistSongsPath.route,
            arguments = listOf(
                navArgument(
                    name = "artistName"
                ) {
                    type = NavType.StringType
                }
            )
        ) {
            val artistName = it.arguments?.getString("artistName")
            if (artistName != null) {
                 ArtistSongsScreen(
                     remoteSongsViewModel = remoteSongsViewModel,
                     navController = navController,
                     artistName = artistName
                 )
            } else {
                navController.popBackStack()
            }
        }

        composable(
            route = Paths.LoginPath.route
        ) {
            Log.d("AppNavigation", "Navigated to LoginPath")
            LoginScreen(
                navController = navController,
                mainViewModel = mainViewModel,
                authViewModel = authViewModel,
            )
        }

        composable(
            route = Paths.RegisterPath.route
        ) {
             RegisterScreen(
                 navController = navController,
                 mainViewModel = mainViewModel,
                 authViewModel = authViewModel,
             )
        }
        composable(
            route = Paths.ManageAccountPath.route
        ) {
              ManageAccountScreen(
                  navController = navController,
                  mainViewModel = mainViewModel,
                  authViewModel = authViewModel,
              )
        }
        composable(
            route = Paths.VerifyEmailPath.route,
            arguments = listOf(
                navArgument(
                    name = "email"
                ) {
                    type = NavType.StringType
                }
            )
        ) {
               VerifyEmailScreen(
                   navController = navController,
                   authViewModel = authViewModel,
                   email = it.arguments?.getString("email") ?: "",
               )
        }
    }
}
