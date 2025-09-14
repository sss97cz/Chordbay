package com.example.chords2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.chords2.data.model.util.Chords
import com.example.chords2.ui.composable.navigation.AppNavigation
import com.example.chords2.ui.theme.ChordsTheme
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("INIT_TEST", "Attempting to access Chords.allBaseChords directly:")
        Chords.allBaseChords
        val songViewModel: SongViewModel by viewModel()
        setContent {
            ChordsTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
//                        modifier = Modifier.padding(innerPadding),
                        viewModel = songViewModel
                    )
                }
//            }
        }
    }
}

