package com.chordbay.app.ui.composable.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chordbay.app.ui.composable.screen.AboutScreen
import com.chordbay.app.ui.composable.screen.ArtistSongsScreen
import com.chordbay.app.ui.composable.screen.EditSongScreen
import com.chordbay.app.ui.composable.screen.HelpScreen
import com.chordbay.app.ui.composable.screen.HomeScreen
import com.chordbay.app.ui.composable.screen.LegalScreen
import com.chordbay.app.ui.composable.screen.PlaylistScreen
import com.chordbay.app.ui.composable.screen.SettingsScreen
import com.chordbay.app.ui.composable.screen.SongScreen
import com.chordbay.app.ui.composable.screen.user.LoginScreen
import com.chordbay.app.ui.composable.screen.user.ManageAccountScreen
import com.chordbay.app.ui.composable.screen.user.RegisterScreen
import com.chordbay.app.ui.composable.screen.user.VerifyEmailScreen
import com.chordbay.app.ui.viewmodel.AuthViewModel
import com.chordbay.app.ui.viewmodel.EditViewModel
import com.chordbay.app.ui.viewmodel.RemoteSongsViewModel
import com.chordbay.app.ui.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = koinViewModel(),
    authViewModel: AuthViewModel = koinViewModel(),
    remoteSongsViewModel: RemoteSongsViewModel = koinViewModel(),
    editViewModel: EditViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Paths.HomePath.route
    ) {
        composable(
            route = Paths.HomePath.route
        ) {
            HomeScreen(
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
                 authViewModel = authViewModel,
             )
        }
        composable(
            route = Paths.ManageAccountPath.route
        ) {
              ManageAccountScreen(
                  navController = navController,
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

        composable(
            route = Paths.HelpPath.route
        ) {
            HelpScreen(navController = navController)
        }

        composable(
            route = Paths.LegalPath.route
        ) {
            LegalScreen(navController = navController)
        }

        composable(
            route = Paths.AboutPath.route
        ){
            AboutScreen(navController = navController)
        }
    }
}
