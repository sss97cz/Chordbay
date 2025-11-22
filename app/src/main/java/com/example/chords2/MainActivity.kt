package com.example.chords2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import com.example.chords2.data.model.util.Chords
import com.example.chords2.data.model.util.Settings
import com.example.chords2.ui.composable.navigation.AppNavigation
import com.example.chords2.ui.theme.ChordsTheme
import com.example.chords2.ui.viewmodel.AuthViewModel
import com.example.chords2.ui.viewmodel.EditViewModel
import com.example.chords2.ui.viewmodel.RemoteSongsViewModel
import com.example.chords2.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Chords.allBaseChords
        Settings.all
        val mainViewModel: MainViewModel by viewModel()
        val authViewModel: AuthViewModel by viewModel()
        val remoteSongsViewModel: RemoteSongsViewModel by viewModel()
        val editViewModel: EditViewModel by viewModel()
        mainViewModel.fetchMyRemoteSongs()
        /*
        TODO: What needs to be done to finish the app?
             - Properly sync the users songs (especially keep track of the IDs from songs/me endpoint
               to not show songs downloaded from remote as your songs and that you have permission to edit them) **DONE**
             - Improve playlist functionality.
             - Fully polish the UI of the app (mainly the song editor and settings, error handling). **Working on**
             - All around code cleanup. **Working on**
             - manage the B,H/A#,B
             - Testing.
             - Add app icon.
             (Optional)
             - Improve the chord detection algorithm.
             - Paging for the song list.
             - Possibility to add more features like TXT export/import.
             - Properly host the MongoDB.
             - Publishing the app to Play Store. (Remove all songs from remote, add privacy policy, kofi link, etc.)
         Bugs:
             - Chords like F# are not properly detected. **Fixed**
             - when are the chords written in one line above the lyrics, the offset up looks wierd
                (maybe fix for later. not really my problem that the user has written the chords in incorrect format)
             - User unable to resend verification email after closing VerifyEmailScreen.
                (need to implement prompt to verify email again if backend detects non verified email)
         */
        setContent {
            ChordsTheme(
                themeMode = mainViewModel.themeMode.collectAsState().value,
                color = mainViewModel.colorMode.collectAsState().value
            ) {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    remoteSongsViewModel = remoteSongsViewModel,
                    editViewModel = editViewModel
                )
            }
        }
    }
}

