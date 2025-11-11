package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.database.playlist.PlaylistEntity
import com.example.chords2.data.datastore.SettingsDataStore
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.util.MainTabs
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.model.util.ThemeMode
import com.example.chords2.data.repository.auth.AuthRepository
import com.example.chords2.data.repository.playlist.PlaylistRepository
import com.example.chords2.data.repository.remote.SongRemoteRepository
import com.example.chords2.data.repository.song.SongRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val songRepository: SongRepository,
    private val songRemoteRepository: SongRemoteRepository,
    private val settingsDataStore: SettingsDataStore,
    private val userDataStore: UserDataStore,
    private val playlistRepository: PlaylistRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    init {
        Log.d("MainViewModel", "Initialized MainViewModel")
        viewModelScope.launch {
            delay(1000)
            authRepository.isUserLoggedIn.collect { token ->
                if (!token) {
                    _myRemoteSongsIds.value = emptySet()
                }
            }
        }
    }

    //----------------------- Settings states - Persistent storage -------------------------------------
    // sort
    val sortOption: StateFlow<SortBy> = settingsDataStore.getSetting(Settings.SortBySetting)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings.SortBySetting.defaultValue
        )

    fun setSortOption(sortOption: SortBy) {
        viewModelScope.launch {
            settingsDataStore.setSetting(Settings.SortBySetting, sortOption)
        }
    }

    // textSize
    val songTextFontSize: StateFlow<Int> = settingsDataStore.getSetting(Settings.FontSize)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings.FontSize.defaultValue
        )

    fun setSongTextFontSize(fontSize: Int) {
        viewModelScope.launch {
            settingsDataStore.setSetting(Settings.FontSize, fontSize)
        }
    }

    // theme
    val themeMode: StateFlow<ThemeMode> = settingsDataStore.getSetting(Settings.ThemeSetting)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings.ThemeSetting.defaultValue
        )

    fun saveThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsDataStore.setSetting(Settings.ThemeSetting, themeMode)
        }
    }

    //---------------- Home Screen states -----------------------------------------------------------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedTab = MutableStateFlow(MainTabs.MY_SONGS)
    val selectedTab: StateFlow<MainTabs> = _selectedTab.asStateFlow()
    fun selectTab(tab: MainTabs) {
        _selectedTab.value = tab
    }

    val _selectedSongsList = MutableStateFlow<List<Song>>(emptyList())
    val selectedSongsList: StateFlow<List<Song>> = _selectedSongsList.asStateFlow()
    fun selectSong(song: Song) {
        val currentList = selectedSongsList.value.toMutableList()
        if (currentList.contains(song)) {
            currentList.remove(song)
        } else {
            currentList.add(song)
        }
        _selectedSongsList.value = currentList
    }

    fun clearSelectedSongs() {
        _selectedSongsList.value = emptyList()
    }


    //-------------------local song CRUD operations ------------------------------------------------
    private val _myRemoteSongsIds = MutableStateFlow<Set<String>>(emptySet())
    val myRemoteSongsIds: StateFlow<Set<String>> = _myRemoteSongsIds.asStateFlow()


    val songs: StateFlow<List<Song>> = combine(
        songRepository.getAllSongs(),
        sortOption,
        searchQuery,
        myRemoteSongsIds
    ) { songs, sortOption, searchQuery, myRemoteSongsIds ->
        Log.d("MainViewModel", "Combining songs with sortOption: $sortOption, searchQuery: '$searchQuery', myRemoteSongsIds size: ${myRemoteSongsIds.size}")
        val markedSongs = songs.map { s ->
            val isSynced = s.remoteId != null && myRemoteSongsIds.contains(s.remoteId)
            s.copy(markSynced = isSynced)
        }
        when (sortOption) {
            SortBy.SONG_NAME -> markedSongs.sortedBy { it.title }
            SortBy.ARTIST_NAME -> markedSongs.sortedBy { it.artist }
        }.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.artist.contains(searchQuery, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getSongById(id: Int): StateFlow<Song?> = songRepository.getSongById(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            songRepository.deleteSong(song)
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch {
            songRepository.updateSong(song)
        }
    }

    fun deleteSongWithOptions(songs: List<Song>, deleteAction: Map<Int, Pair<Boolean, Boolean>>) {
        viewModelScope.launch {
            for (song in songs) {
                val action = deleteAction[song.localId]
                if (action != null) {
                    val (deleteLocal, deleteRemote) = action
                    if (deleteRemote && song.remoteId != null) {
                        deleteRemoteSong(song)
                    }
                    if (deleteLocal) {
                        deleteSong(song)
                    }
                }
            }
        }
    }

    //------------------- Remote songs operations ------------------------------------------------


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    fun clearError() {
        _error.value = null
    }


    private val _remoteSongById = MutableStateFlow<Song?>(null)

    //TODO("delete this and change the implementation to use function from remoteSongViewModel")
    val remoteSongById: StateFlow<Song?> = _remoteSongById.asStateFlow()

    fun getRemoteSongById(id: String) {
        Log.d("SongViewModel", "getRemoteSongById called with id: $id")
        viewModelScope.launch {
            val token = authRepository.getAccessToken()

            var result =
                runCatching { songRemoteRepository.getSongById(id, token) } // having token nullable

            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: ""
                if (message.contains("401")) {
                    val refreshResult = authRepository.refresh()
                    if (refreshResult.isSuccess) {
                        val newToken = authRepository.getAccessToken()
                        if (newToken != null) {
                            result = runCatching { songRemoteRepository.getSongById(id, newToken) }
                        }
                    }
                }
            }
            result.onSuccess { fetchedSong ->
                val song = fetchedSong.getOrNull()
                _remoteSongById.value = song
                Log.d("SongViewModel", "Fetched remote song successfully: $fetchedSong")
            }.onFailure { exception ->
                _error.value = "Failed to fetch remote song: ${exception.message}"
                Log.e("SongViewModel", "Error fetching remote song: ${exception.message}")
            }
        }
    }

    //TODO("move this function to remoteSongsViewModel")
    fun postSong(song: Song) {
        val song = song.copy(
            title = song.title.ifBlank { "Untitled" }.trim(),
            artist = song.artist.ifBlank { "Unknown Artist" }.trim()
        )
        val isPost = song.remoteId == null
        Log.d("SongViewModel", "isPost: $isPost")
        viewModelScope.launch {
            val token = authRepository.getAccessToken()
            if (token == null) {
                _error.value = "User not authenticated. Please log in."
                return@launch
            }

            if (isPost) {
                var result = songRemoteRepository.createSong(song, token)
                Log.d("SongViewModel", "Posting new song: $result")
                // If unauthorized, try refresh + retry
                if (result.isFailure) {
                    val message = result.exceptionOrNull()?.message ?: ""
                    if (message.contains("401")) {
                        val refreshResult = authRepository.refresh()
                        if (refreshResult.isSuccess) {
                            val newToken = authRepository.getAccessToken()
                            if (newToken != null) {
                                result = songRemoteRepository.createSong(song, newToken)
                            }
                        }
                    }
                }
                result.onSuccess {
                    Log.d("SongViewModel", "Song posted successfully with ID: $it")
                    updateSong(song.copy(remoteId = it)).also { _ ->
                        _myRemoteSongsIds.value += it
                        Log.d("SongViewModel", "Local song updated with remote ID: $it")
                    }
                }.onFailure { exception ->
                    _error.value = "Failed to post song: ${exception.message}"
                }

            } else {
                Log.d("SongViewModel", "Updating song: $song")

                var result = songRemoteRepository.updateSong(song, token)

                // If unauthorized, try refresh + retry
                if (result.isFailure) {
                    val message = result.exceptionOrNull()?.message ?: ""
                    if (message.contains("401")) {
                        val refreshResult = authRepository.refresh()
                        if (refreshResult.isSuccess) {
                            val newToken = authRepository.getAccessToken()
                            if (newToken != null) {
                                result = songRemoteRepository.updateSong(song, newToken)
                                result.onFailure {
                                    if (it.message?.contains("403") == true || it.message?.contains(
                                            "404"
                                        ) == true
                                    ) {
                                        val result = songRemoteRepository.createSong(
                                            token = newToken,
                                            song = song.copy(remoteId = null)
                                        )
                                        result.onSuccess {
                                            Log.d(
                                                "SongViewModel",
                                                "Song posted successfully with ID: $it"
                                            )
                                            updateSong(song.copy(remoteId = it)).also { _ ->
                                                Log.d(
                                                    "SongViewModel",
                                                    "Local song updated with remote ID: $it"
                                                )
                                                _myRemoteSongsIds.value += it
                                            }
                                        }.onFailure { exception ->
                                            _error.value =
                                                "Failed to post song: ${exception.message}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                result.onSuccess {
                    if (it) {
                        Log.d("SongViewModel", "Song updated successfully on remote server")
                    } else {
                        _error.value = "Failed to update song: Unknown error"
                    }
                }.onFailure { exception ->
                    _error.value = "Failed to update song: ${exception.message}"
                    val message = exception.message ?: ""
                    if (message.contains("403") || message.contains("404")) {
                        val result = songRemoteRepository.createSong(
                            token = token,
                            song = song.copy(remoteId = null)
                        )
                        result.onSuccess {
                            Log.d("SongViewModel", "Song posted successfully with ID: $it")
                            updateSong(song.copy(remoteId = it)).also {
                                Log.d("SongViewModel", "Local song updated with remote ID: $it")
                            }
                        }.onFailure { exception ->
                            _error.value = "Failed to post song: ${exception.message}"
                        }
                    }
                }
            }
        }
    }

    //TODO(Move this function to remoteSongsViewModel)
    fun postSongs(songs: List<Song>) {
        songs.forEach { song ->
            postSong(song)
        }
    }

    //TODO(Move this function to remoteSongsViewModel)
    fun fetchMyRemoteSongs() {
        Log.d("SongViewModel", "fetchMyRemoteSongs called")
        viewModelScope.launch {
            val token = authRepository.getAccessToken() ?: run {
                Log.d("SongViewModel", "No access token available")
                _error.value = "User not authenticated. Please log in."
                _myRemoteSongsIds.value = emptySet()
                return@launch
            }

            val firstResult = songRemoteRepository.getMySongs(token)
            if (firstResult.isFailure) {
                val message = firstResult.exceptionOrNull()?.message.orEmpty()
                if (message.contains("401")) {
                    val refreshed = authRepository.refresh()
                    if (refreshed.isSuccess) {
                        val newToken = authRepository.getAccessToken()
                        if (newToken != null) {
                            val retry = songRemoteRepository.getMySongs(newToken)
                            retry.onSuccess { remoteSongs ->
                                syncToLocalDb(remoteSongs)
                                _myRemoteSongsIds.value = remoteSongs.mapNotNull { it.remoteId }.toSet()
                                return@launch // done
                            }.onFailure { e ->
                                _myRemoteSongsIds.value = emptySet()
                                _error.value = "Failed after refresh: ${e.message}"
                                return@launch
                            }
                        }
                    }
                    // refresh failed
                    Log.d("SongViewModel", "Token refresh failed")
                    _error.value = "Refresh failed: $message"
                    _myRemoteSongsIds.value = emptySet()
                    return@launch
                }
            }
            // Success path (or non-401 failure)
            firstResult.onSuccess { remoteSongs ->
                syncToLocalDb(remoteSongs)
                _myRemoteSongsIds.value = remoteSongs.mapNotNull { it.remoteId }.toSet()
            }.onFailure { e ->
                Log.d("SongViewModel", "Failed to fetch my songs: ${e.message}")
                _myRemoteSongsIds.value = emptySet()
                _error.value = "Failed to fetch my songs: ${e.message}"
            }
        }
    }

    private suspend fun syncToLocalDb(remoteSongs: List<Song>) {
        val localSongs = songRepository.getAllSongs().first()

        remoteSongs.forEach { remoteSong ->
            val exists = localSongs.any { it.remoteId == remoteSong.remoteId }
            if (!exists) {
                songRepository.insertSong(remoteSong.copy(localId = null))
            }
        }
    }

    //TODO(Move this function to remoteSongsViewModel)
    fun applyPrivacyAndPost(
        songs: List<Song>,
        defaultIsPublic: Boolean,
        overrides: Map<Int, Boolean>
    ) {
        // Note: this assumes Song has an isPublic: Boolean property.
        val songsWithPrivacy = songs.map { s ->
            val localId = s.localId
            val resolved = localId?.let { overrides[it] } ?: defaultIsPublic
            s.copy(isPublic = resolved)
        }
        postSongs(songsWithPrivacy)
    }

    fun deleteRemoteSong(song: Song) {
        viewModelScope.launch {
            val token = authRepository.getAccessToken()
            if (token == null) {
                _error.value = "User not authenticated. Please log in."
                return@launch
            }
            if (song.remoteId == null) {
                _error.value = "Cannot delete song: remoteId is null"
                return@launch
            }
            var result = songRemoteRepository.deleteSong(song.remoteId, token)

            // If unauthorized, try refresh + retry
            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: ""
                if (message.contains("401")) {
                    val refreshResult = authRepository.refresh()
                    if (refreshResult.isSuccess) {
                        val newToken = authRepository.getAccessToken()
                        if (newToken != null) {
                            result = songRemoteRepository.deleteSong(song.remoteId, newToken)
                        }
                    }
                }
            }
            result.onSuccess {
                if (it) {
                    Log.d("SongViewModel", "Song deleted successfully from remote server")
//                    deleteSong(song)
                } else {
                    _error.value = "Failed to delete song: Unknown error"
                }
            }.onFailure { exception ->
                _error.value = "Failed to delete song: ${exception.message}"
            }
        }
    }

    //------------------------------- Playlist  operations ---------------------------------------------

    val playlists: StateFlow<List<PlaylistEntity>> = playlistRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.makePlaylist(name)
        }
    }

    fun addSongToPlaylist(playlistId: Int, song: Song) {
        viewModelScope.launch {
            if (song.localId != null) {
                playlistRepository.addSongToPlaylist(playlistId, song)
            } else {
                Log.e("SongViewModel", "Cannot add song to playlist: song.localId is null")
            }
        }
    }

    fun deletePlaylist(id: Int) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlists.value.first { it.id == id })
        }
    }


    fun getSongsInPlaylist(playlistId: Int): StateFlow<List<Song>> =
        playlistRepository.getSongsInPlaylist(playlistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getPlaylistById(id: Int): StateFlow<PlaylistEntity?> =
        playlists.combine(playlists) { playlists, _ ->
            playlists.firstOrNull { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

}