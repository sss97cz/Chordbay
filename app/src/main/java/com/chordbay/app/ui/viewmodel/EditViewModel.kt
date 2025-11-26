package com.chordbay.app.ui.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope
import com.chordbay.app.data.datastore.SettingsDataStore
import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.util.HBFormat
import com.chordbay.app.data.model.util.Settings
import com.chordbay.app.data.repository.song.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditViewModel(
    private val songRepository: SongRepository,
    private val settingsDataStore: SettingsDataStore
): ViewModel() {
    private val _songName = MutableStateFlow<String?>(null)
    val songName = _songName.asStateFlow()
    fun setSongName(name: String) {
        _songName.value = name
        Log.d("SongViewModel", name)
    }

   private val savedHbFormat: StateFlow<HBFormat> = settingsDataStore.getSetting(Settings.HBFormatSetting)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HBFormat.ENG
        )
    private val _hbFormat: MutableStateFlow<HBFormat> = MutableStateFlow(
        savedHbFormat.value
    )
    val hbFormat: StateFlow<HBFormat> = _hbFormat.asStateFlow()
    fun setHbFormat(format: HBFormat) {
        _hbFormat.value = format
    }


    private val _songArtist = MutableStateFlow<String>("")
    val songArtist = _songArtist.asStateFlow()
    fun setSongArtist(artist: String) {
        _songArtist.value = artist
    }

    private val _songContent = MutableStateFlow<TextFieldValue>(TextFieldValue(""))
    val songContent = _songContent.asStateFlow()
    fun setSongContent(content: TextFieldValue) {
        _songContent.value = content
    }

    fun clearSongStates() {
        _songName.value = null
        _songArtist.value = ""
        _songContent.value = TextFieldValue("")
        _remoteId.value = null
        setHasLoadedEdit(false)
        Log.d("SongViewModel", "song states reset")
    }

    private val _hasLoadedEdit = MutableStateFlow(false)
    val hasLoadedEdit = _hasLoadedEdit.asStateFlow()
    fun setHasLoadedEdit(loaded: Boolean) {
        _hasLoadedEdit.value = loaded
        Log.d("SongViewModel", "hasLoadedEdit set to $loaded")
    }

    private val _remoteId = MutableStateFlow<String?>(null)
    fun saveEditedSong(songId: String) {
        viewModelScope.launch {
            Log.d("SongViewModel", "Saving song with HBFormat: $hbFormat")
            if (songId == "new") {
                songRepository.insertSong(
                    Song(
                        localId = null,
                        remoteId = null,
                        title = songName.value ?: "",
                        artist = songArtist.value,
                        content = songContent.value.text,
                        hBFormat = hbFormat.value
                    )
                )
            } else {
                songRepository.updateSong(
                    Song(
                        localId = songId.toInt(),
                        remoteId = _remoteId.value,
                        title = songName.value ?: "",
                        artist = songArtist.value,
                        content = songContent.value.text,
                        hBFormat = hbFormat.value
                    ).also { Log.d("SongViewModel", "$it") }
                )
            }
        }
    }

    fun loadEditSong(songId: String) {
        viewModelScope.launch {
            if (songId != "new") {
                val song = songRepository.getSongById(songId.toInt()).first()
                if (song != null) {
                    _songName.value = song.title
                    _songArtist.value = song.artist
                    _songContent.value = TextFieldValue(song.content)
                    _remoteId.value = song.remoteId
                    _hbFormat.value = song.hBFormat
                    Log.d("SongViewModel", "Loaded song: $song")
                } else {
                    Log.e("SongViewModel", "No song found with ID: $songId")
                }
            }
            setHasLoadedEdit(true)
        }
    }
}