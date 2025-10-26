package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.database.playlist.PlaylistEntity
import com.example.chords2.data.datastore.SettingsDataStore
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.util.Chords
import com.example.chords2.data.model.util.MainTabs
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.model.util.ThemeMode
import com.example.chords2.data.remote.model.ArtistDto
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SongViewModel(
    private val songRepository: SongRepository,
    private val songRemoteRepository: SongRemoteRepository,
    private val settingsDataStore: SettingsDataStore,
    private val userDataStore: UserDataStore,
    private val playlistRepository: PlaylistRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    init {
        Log.d("SongViewModel", "Initialized SongViewModel")
        viewModelScope.launch {
            delay(500)
            fetchMyRemoteSongs()
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
    val _myRemoteSongsIds = MutableStateFlow<Set<String>>(emptySet())

    val songs: StateFlow<List<Song>> = combine(
        songRepository.getAllSongs(),
        sortOption,
        searchQuery,
        _myRemoteSongsIds
    ) { songs, sortOption, searchQuery, myRemoteSongsIds ->
        val markedSongs = songs.map {
            Log.d("SongViewModel", "Checking song: ${it.title} with remoteId: ${it.remoteId}")
            if (it.remoteId != null && myRemoteSongsIds.contains(it.remoteId)) {
                Log.d("SongViewModel", "Marking song as synced: ${it.title}")
                it.copy(markSynced = true)
            } else
                it
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

    fun insertSong(song: Song) {
        viewModelScope.launch {
            songRepository.insertSong(song)
        }
    }

    suspend fun addNewSongAndGetId(): Long {
        return songRepository.insertSong()
    }

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

    fun deleteSongRemoteLocal(song: Song) {
        viewModelScope.launch {
            if (song.remoteId != null) {
                deleteRemoteSong(song)
            } else {
                deleteSong(song)
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

    //------------------- Remote songs operations ------------------------------------------------
    private val _remoteSongs = MutableStateFlow<List<Song>>(emptyList())
    val remoteSongs: StateFlow<List<Song>> = combine(
        sortOption,
        _remoteSongs,
        searchQuery
    ) { sortOption, songs, searchQuery ->
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


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    fun clearError() {
        _error.value = null
    }

    fun saveSongToDatabase(song: Song) =
        viewModelScope.launch {
            songRepository.insertRemoteSong(song)
        }

    private val _remoteSongById = MutableStateFlow<Song?>(null)
    val remoteSongById: StateFlow<Song?> = _remoteSongById.asStateFlow()

    fun getRemoteSongById(id: String) { // TODO("fix this function")
        Log.d("SongViewModel", "getRemoteSongById called with id: $id")
        viewModelScope.launch {
            val token = authRepository.getAcessToken()
            if (token == null) {
                _error.value = "User not authenticated. Please log in."
                return@launch
            }
            var result = runCatching { songRemoteRepository.getSongById(id, token) }

            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: ""
                if (message.contains("401")) {
                    val refreshResult = authRepository.refresh()
                    if (refreshResult.isSuccess) {
                        val newToken = authRepository.getAcessToken()
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

    fun postSong(song: Song) {
        val isPost = song.remoteId == null
        Log.d("SongViewModel", "isPost: $isPost")
        viewModelScope.launch {
            val token = authRepository.getAcessToken()
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
                            val newToken = authRepository.getAcessToken()
                            if (newToken != null) {
                                result = songRemoteRepository.createSong(song, newToken)
                            }
                        }
                    }
                }
                result.onSuccess {
                    Log.d("SongViewModel", "Song posted successfully with ID: $it")
                    updateSong(song.copy(remoteId = it)).also {
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
                            val newToken = authRepository.getAcessToken()
                            if (newToken != null) {
                                result = songRemoteRepository.updateSong(song, newToken)
                                result.onFailure {
                                    if (it.message?.contains("403") == true || it.message?.contains("404") == true) {
                                        val result = songRemoteRepository.createSong(
                                            token = newToken,
                                            song = song.copy(remoteId = null)
                                        )
                                        result.onSuccess {
                                            Log.d(
                                                "SongViewModel",
                                                "Song posted successfully with ID: $it"
                                            )
                                            updateSong(song.copy(remoteId = it)).also {
                                                Log.d(
                                                    "SongViewModel",
                                                    "Local song updated with remote ID: $it"
                                                )
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


    fun postSongs(songs: List<Song>) {
        songs.forEach { song ->
            postSong(song)
        }
    }

    val _selectedRemoteSongs = MutableStateFlow<List<Song>>(emptyList())
    val selectedRemoteSongs: StateFlow<List<Song>> = _selectedRemoteSongs.asStateFlow()
    fun selectRemoteSong(song: Song) {
        val currentList = selectedRemoteSongs.value.toMutableList()
        if (currentList.contains(song)) {
            currentList.remove(song)
        } else {
            currentList.add(song)
        }
        _selectedRemoteSongs.value = currentList
    }

    fun clearSelectedRemoteSongs() {
        _selectedRemoteSongs.value = emptyList()
    }

    fun saveSelectedRemoteSongsToDatabase() {
        for (song in selectedRemoteSongs.value) {
            saveSongToDatabase(song)
        }
    }

    val _artists: MutableStateFlow<List<ArtistDto>> = MutableStateFlow(emptyList())
    val artists = _artists.asStateFlow()
    fun fetchAllArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            songRemoteRepository.getAllArtists()
                .onSuccess { fetchedArtists ->
                    _artists.value = fetchedArtists
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = "Failed to fetch artists: ${exception.message}"
                    _isLoading.value = false
                }
        }
    }

    fun getSongsByArtist(artist: String): StateFlow<List<Song>> {
        Log.d("SongViewModel", "getSongsByArtist called with artist: $artist")
        val artistSongs = MutableStateFlow<List<Song>>(emptyList())
        viewModelScope.launch {
            songRemoteRepository.getSongsByArtist(artist)
                .onSuccess { fetchedSongs ->
                    artistSongs.value = fetchedSongs
                    Log.d("SongViewModel", "Fetched ${fetchedSongs.size} songs for artist: $artist")
                }
                .onFailure { exception ->
                    _error.value = "Failed to fetch songs by artist: ${exception.message}"
                    Log.e(
                        "SongViewModel",
                        "Error fetching songs for artist $artist: ${exception.message}"
                    )
                }
        }
        return artistSongs
    }

    fun fetchMyRemoteSongs() {
        viewModelScope.launch {
            val token = authRepository.getAcessToken()
            if (token == null) {
                _error.value = "User not authenticated. Please log in."
                return@launch
            }
            val result = songRemoteRepository.getMySongs(token)
            if (result.isFailure) {
                val message = result.exceptionOrNull()?.message ?: ""
                if (message.contains("401")) {
                    val refreshResult = authRepository.refresh()
                    if (refreshResult.isSuccess) {
                        val newToken = authRepository.getAcessToken()
                        if (newToken != null) {
                            val retryResult = songRemoteRepository.getMySongs(newToken)
                            retryResult.onSuccess { remoteSongs ->
                                syncToLocalDb(remoteSongs)
                                _myRemoteSongsIds.value = remoteSongs.mapNotNull { it.remoteId }.toSet()
                                Log.d("SongViewModel", "Synced ${remoteSongs.size} songs to local DB")
                            }.onFailure { exception ->
                                _error.value = "Failed to fetch my songs: ${exception.message}"
                            }
                        }
                    }
                }
            }
            result.onSuccess { remoteSongs ->
                syncToLocalDb(remoteSongs)
                _myRemoteSongsIds.value = remoteSongs.mapNotNull { it.remoteId }.toSet()
                Log.d("SongViewModel", "Synced ${remoteSongs.size} songs to local DB")
            }
                .onFailure { exception ->
                    _error.value = "Failed to fetch my songs: ${exception.message}"

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
            val token = authRepository.getAcessToken()
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
                        val newToken = authRepository.getAcessToken()
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


    //------------------- Edit Song Screen states ------------------------------------------------
    private val _songName = MutableStateFlow<String?>(null)
    val songName = _songName.asStateFlow()
    fun setSongName(name: String) {
        _songName.value = name
        Log.d("SongViewModel", name)
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
        Log.d("SongViewModel", "song states resert")
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
            if (songId == "new") {
                songRepository.insertSong(
                    Song(
                        localId = null,
                        remoteId = null,
                        title = songName.value ?: "",
                        artist = songArtist.value,
                        content = songContent.value.text
                    )
                )
            } else {
                songRepository.updateSong(
                    Song(
                        localId = songId.toInt(),
                        remoteId = _remoteId.value,
                        title = songName.value ?: "",
                        artist = songArtist.value,
                        content = songContent.value.text
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
                    Log.d("SongViewModel", "Loaded song: $song")
                } else {
                    Log.e("SongViewModel", "No song found with ID: $songId")
                }
            }
            setHasLoadedEdit(true)
        }
    }


}