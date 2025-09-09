package com.example.chords2.ui.composable.navigation

sealed class Paths(
    val route : String
){
    object HomePath: Paths("home")
    object SongPath: Paths("song/{songId}"){
        fun createRoute(songId : String) = "song/$songId"
    }
    object EditSongPath: Paths("editSong/{songId}"){
        fun createRoute(songId : String) = "editSong/$songId"
    }
}
