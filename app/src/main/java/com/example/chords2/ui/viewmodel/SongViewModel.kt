package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Chords
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.SortBy
import com.example.chords2.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val _sortOption = MutableStateFlow(SortBy.SONG_NAME)
    fun setSortOption(sortOption: SortBy) {
        Log.d("SongViewModel", "Setting sort option to: $sortOption")
        _sortOption.value = sortOption
    }
    private val _searchQuery = MutableStateFlow("")
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val songs: StateFlow<List<SongEntity>> = combine(
        songRepository.getAllSongs(),
        _sortOption,
        _searchQuery
    ) { songs, sortOption, searchQuery ->
        when (sortOption) {
            SortBy.SONG_NAME -> songs.sortedBy { it.title }
            SortBy.ARTIST_NAME -> songs.sortedBy { it.artist }
        }.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.artist.contains(searchQuery, ignoreCase = true)
        }
    }.stateIn(
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

    suspend fun addNewSongAndGetId(song: Song = Song()): Long {
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

    fun deleteAll() {
        viewModelScope.launch {
            songs.value.forEach {
                songRepository.deleteSong(it)
            }
        }
    }

    fun findKey(song: String): String? {
        val openBracketIndex = song.indexOf('[')
        if (openBracketIndex == -1) {
            return null
        }
        val textAfterOpenBracket = song.substring(startIndex = openBracketIndex)
        val closeBracketIndexInSubstring = textAfterOpenBracket.indexOf(']')
        if (closeBracketIndexInSubstring == -1) {
            return null
        }
        val firstChord = textAfterOpenBracket.substring(1)
            .substringBefore(']')
        if (firstChord.isEmpty()) {
            return null
        }
        val baseChord = Chords.allBaseChords.firstOrNull {
            firstChord.contains(it.value)
        }
        return baseChord?.value
    }
}