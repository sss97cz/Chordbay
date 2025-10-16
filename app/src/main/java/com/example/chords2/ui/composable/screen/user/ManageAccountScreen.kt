package com.example.chords2.ui.composable.screen.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.chords2.ui.viewmodel.AuthViewModel
import com.example.chords2.ui.viewmodel.SongViewModel

@Composable
fun ManageAccountScreen(
    navController: NavController,
    songViewModel: SongViewModel,
    authViewModel: AuthViewModel
){
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Text("TODO: ManageAccountScreen")
    }
}