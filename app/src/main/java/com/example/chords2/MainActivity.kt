package com.example.chords2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.core.view.WindowCompat
import com.example.chords2.data.model.util.Chords
import com.example.chords2.data.model.util.Settings
import com.example.chords2.ui.composable.navigation.AppNavigation
import com.example.chords2.ui.theme.ChordsTheme
import com.example.chords2.ui.viewmodel.AuthViewModel
import com.example.chords2.ui.viewmodel.RemoteSongsViewModel
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Chords.allBaseChords
        Settings.all
        val songViewModel: SongViewModel by viewModel()
        val authViewModel: AuthViewModel by viewModel()
        val remoteSongsViewModel: RemoteSongsViewModel by viewModel()
        /*
        TODO: What needs to be done to finish the app?
             - Properly sync the users songs (especially keep track of the IDs from songs/me endpoint
               to not show songs downloaded from remote as your songs and that you have permission to edit them) **Working on**
             - Improve playlist functionality.
             - Fully polish the UI of the app (mainly the song editor and settings, error handling).
             - All around code cleanup.
             - Testing.
             - Add app icon.
             (Optional)
             - Improve the chord detection algorithm.
             - Possibility to add more features like TXT export/import.
             - Properly host the MongoDB.
             - Publishing the app to Play Store. (Remove all songs from remote, add privacy policy, kofi link, etc.)
         */
        setContent {
            ChordsTheme(
                themeMode = songViewModel.themeMode.collectAsState().value
            ) {
                AppNavigation(
                    songViewModel = songViewModel,
                    authViewModel = authViewModel,
                    remoteSongsViewModel = remoteSongsViewModel
                )
            }
        }
    }
}

