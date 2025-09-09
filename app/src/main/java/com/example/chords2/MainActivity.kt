package com.example.chords2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.chords2.ui.composable.navigation.AppNavigation
import com.example.chords2.ui.theme.ChordsTheme
import com.example.chords2.ui.viewmodel.SongViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

