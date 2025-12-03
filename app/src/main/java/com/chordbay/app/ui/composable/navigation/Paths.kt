package com.chordbay.app.ui.composable.navigation

sealed class Paths(
    val route : String
){
    object HomePath: Paths("home")
    object SongPath: Paths("song/{songId}"){
        fun createRoute(songId : String) = "song/$songId"
    }
    object SongFromPlaylistPath: Paths("songFromPlaylist/{songId}/{playlistId}"){
        fun createRoute(songId : String, playlistId: Int) = "songFromPlaylist/$songId/$playlistId"
    }
    object EditSongPath: Paths("editSong/{songId}"){
        fun createRoute(songId : String) = "editSong/$songId"
    }
    object RemoteSongPath: Paths("remoteSong/{remoteId}"){
        fun createRoute(remoteId : String) = "remoteSong/$remoteId"
    }
    object SettingsPath: Paths("settings")

    object PlaylistPath: Paths("playlist/{playlistId}"){
        fun createRoute(playlistId : Int) = "playlist/$playlistId"
    }
    object ArtistSongsPath: Paths("artistSongs/{artistName}"){
        fun createRoute(artistName : String) = "artistSongs/$artistName"
    }
    object LoginPath: Paths("login")

    object RegisterPath: Paths("register")

    object ManageAccountPath: Paths("manageAccount")

    object VerifyEmailPath: Paths("verifyEmail/{email}"){
        fun createRoute(email : String) = "verifyEmail/$email"
    }
    object HelpPath: Paths("help")
    object LegalPath: Paths("legal")
    object AboutPath: Paths("about")
}
