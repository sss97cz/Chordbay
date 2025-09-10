package com.example.chords2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Song
import com.example.chords2.data.repository.SongRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SongViewModel(private val songRepository: SongRepository) : ViewModel() {
    //
//    val count = mutableIntStateOf(
//        10
//    )
//    fun getAllSongs() = songRepository.getAllSongs()
//    fun insertSong(song: Song) {
//        viewModelScope.launch {
//            songRepository.insertSong(song)
//        }
//    }
//
//    fun deleteDong(song: Song) {
//        viewModelScope.launch {
//            songRepository.insertSong(song)
//        }
//    }
//
//    fun getSongById(id: String) = songRepository.getSongById(id)
//
//    fun updateSong(song: Song) = songRepository.updateSong(song)
//


    val songs: StateFlow<List<SongEntity>> = songRepository.getAllSongs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getSongById(id: Int): StateFlow<SongEntity?> = songRepository.getSongById(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun insertSong(song: Song) {
        viewModelScope.launch {
            val newSong = SongEntity(
                title = song.title,
                artist = song.artist,
                content = song.content
            )
            songRepository.insertSong(newSong)
        }
    }
    suspend fun addNewSongAndGetId(song: Song = Song()): Long{
        return songRepository.insertSong(
            SongEntity(
                title = song.title,
                artist = song.artist,
                content = song.content
            )
        )
    }
    fun deleteSong(song: SongEntity) {
        viewModelScope.launch {
            songRepository.deleteSong(song)
        }
    }
    fun updateSong(song: SongEntity) {
        viewModelScope.launch {
            val songToUpdate = SongEntity(
                id = song.id,
                title = song.title,
                artist = song.artist,
                content = song.content
            )
            songRepository.updateSong(songToUpdate)
        }
    }
    fun deleteAll(){
        viewModelScope.launch {
            songs.value.forEach {
                songRepository.deleteSong(it)
            }
        }
    }
}