package com.chordbay.app.ui.viewmodel

import android.app.Notification
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.chordbay.app.data.database.playlist.PlaylistEntity
import com.chordbay.app.data.datastore.FirstLaunchDatastore
import com.chordbay.app.data.datastore.NotificationDataStore
import com.chordbay.app.data.datastore.SettingsDataStore
import com.chordbay.app.data.datastore.UserDataStore
import com.chordbay.app.data.helper.AppVersion
import com.chordbay.app.data.helper.TxtSongIO
import com.chordbay.app.data.helper.getFileName
import com.chordbay.app.data.model.PlaylistInfo
import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.util.ColorMode
import com.chordbay.app.data.model.chord.HBFormat
import com.chordbay.app.data.model.chord.MollFormat
import com.chordbay.app.data.model.util.MainTabs
import com.chordbay.app.data.model.settings.Settings
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.data.model.util.ThemeMode
import com.chordbay.app.data.model.util.toError
import com.chordbay.app.data.remote.model.NotificationDto
import com.chordbay.app.data.repository.auth.AuthRepository
import com.chordbay.app.data.repository.playlist.PlaylistRepository
import com.chordbay.app.data.repository.remote.SongRemoteRepository
import com.chordbay.app.data.repository.song.SongRepository
import com.google.gson.internal.GsonBuildConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val songRepository: SongRepository,
    private val songRemoteRepository: SongRemoteRepository,
    private val settingsDataStore: SettingsDataStore,
    private val userDataStore: UserDataStore,
    private val playlistRepository: PlaylistRepository,
    private val authRepository: AuthRepository,
    private val firstLaunchDataStore: FirstLaunchDatastore,
    private val notificationDataStore: NotificationDataStore,
    private val appVersion: AppVersion
) : ViewModel() {

    init {
        Log.d("MainViewModel", "Initialized MainViewModel")
        viewModelScope.launch {
            delay(1000)
            authRepository.isUserLoggedIn.collect {
                if (!it) {
                    _myRemoteSongsIds.value = emptySet()
                } else {
                    fetchMyRemoteSongs()
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

    // color mode
    val colorMode: StateFlow<ColorMode> = settingsDataStore.getSetting(Settings.ColorModeSetting)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings.ColorModeSetting.defaultValue
        )

    fun saveColorMode(colorMode: ColorMode) {
        viewModelScope.launch {
            settingsDataStore.setSetting(Settings.ColorModeSetting, colorMode)
        }
    }

    // HB Format
    val hbFormat: StateFlow<HBFormat> = settingsDataStore.getSetting(Settings.HBFormatSetting)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings.HBFormatSetting.defaultValue
        )

    fun saveHBFormat(format: HBFormat) {
        viewModelScope.launch {
            settingsDataStore.setSetting(Settings.HBFormatSetting, format)
        }
    }

    val isFirstLaunch: StateFlow<Boolean> = firstLaunchDataStore.isFirstLaunch()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setNotFirstLaunch() {
        viewModelScope.launch {
            firstLaunchDataStore.setFirstLaunch()
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
        Log.d(
            "MainViewModel",
            "Combining songs with sortOption: $sortOption, searchQuery: '$searchQuery', myRemoteSongsIds size: ${myRemoteSongsIds.size}"
        )
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

    fun insertSong(song: Song) {
        viewModelScope.launch {
            songRepository.insertSong(song)
        }
    }

    fun updateSong(song: Song) {
        viewModelScope.launch {
            songRepository.updateSong(song)
        }
    }

    val isDoneDeletingSongs = MutableStateFlow<Boolean?>(null)
    fun clearDoneDeletingSongs() {
        isDoneDeletingSongs.value = null
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
            if (deleteAction.entries.any { it.value.second }) {
                isDoneDeletingSongs.value = true
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
                _error.value = "Failed to fetch remote song: ${exception.message}".toError().message
                Log.e("SongViewModel", "Error fetching remote song: ${exception.message}")
            }
        }
    }

    private val _postSuccess = MutableStateFlow<Boolean?>(null)
    val postSuccess = _postSuccess.asStateFlow()
    fun clearPostSuccess() {
        _postSuccess.value = null
    }

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
                        _postSuccess.value = true
                        Log.d("SongViewModel", "Local song updated with remote ID: $it")
                    }
                }.onFailure { exception ->
                    _error.value = "Failed to post song: ${exception.message?.toError()?.message}"
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
                                    if (it.message?.contains("403") == true ||
                                        it.message?.contains("404") == true
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
                                            _postSuccess.value = true
                                        }.onFailure { exception ->
                                            _error.value =
                                                "Failed to post song: ${exception.message?.toError()?.message}"
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
                        _postSuccess.value = true
                    } else {
                        _error.value = "Failed to update song: Unknown error"
                    }
                }.onFailure { exception ->
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
                            _postSuccess.value = true
                            _myRemoteSongsIds.value += it
                        }.onFailure { exception ->
                            _error.value =
                                "Failed to post song: ${exception.message?.toError()?.message}"
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
                                _myRemoteSongsIds.value =
                                    remoteSongs.mapNotNull { it.remoteId }.toSet()
                                return@launch // done
                            }.onFailure { e ->
                                _myRemoteSongsIds.value = emptySet()
                                _error.value =
                                    "Failed after refresh: ${e.message?.toError()?.message}"
                                return@launch
                            }
                        }
                    }
                    // refresh failed
                    Log.d("SongViewModel", "Token refresh failed")
                    _error.value = "Refresh failed: ${message?.toError()?.message}"
                    _myRemoteSongsIds.value = emptySet()
                    return@launch
                }
            }
            // Success path (or non-401 failure)
            firstResult.onSuccess { remoteSongs ->
                syncToLocalDb(remoteSongs)
                _myRemoteSongsIds.value = remoteSongs.mapNotNull { it.remoteId }.toSet()
            }.onFailure { e ->
                Log.d("SongViewModel", "Failed to fetch my songs: ${e.message?.toError()?.message}")
                _myRemoteSongsIds.value = emptySet()
                _error.value = "Failed to fetch my songs: ${e.message?.toError()?.message}"
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

    var deleteSuccess = MutableStateFlow<Boolean?>(null)
    fun clearDeleteSuccess() {
        deleteSuccess.value = null
    }

    fun deleteRemoteSong(song: Song) {
        viewModelScope.launch {
            val token = authRepository.getAccessToken()
            if (token == null) {
                _error.value = "User not authenticated. Please log in."
                return@launch
            }
            if (song.remoteId == null) {
                _error.value = "Cannot delete song"
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
                    if (song.localId != null) {
                        val updatedSong = song.copy(remoteId = null)
                        updateSong(updatedSong)
                    }
                    _myRemoteSongsIds.value = _myRemoteSongsIds.value - song.remoteId
                    deleteSuccess.value = true

                } else {
                    _error.value = "Failed to delete song: Unknown error"
                }
            }.onFailure { exception ->
                _error.value = "Failed to delete song: ${exception.message?.toError()?.message}"
            }
        }
    }

    //------------------------------- Txt import/export -----------------------------------------------
    fun importTxtSongsFromUris(
        uris: List<@JvmSuppressWildcards Uri>,
        context: Context,
        hbFormat: HBFormat
    ) {
        viewModelScope.launch {
            if (uris.isNotEmpty()) {
                uris.forEach { uri ->
                    try {
                        val fileName = context.contentResolver.getFileName(uri) ?: "Imported.txt"
                        val raw = context.contentResolver.openInputStream(uri)
                            ?.bufferedReader(Charsets.UTF_8)
                            ?.readText()
                        if (!raw.isNullOrEmpty()) {
                            val song = TxtSongIO.txtToSong(
                                rawContent = raw,
                                fileName = fileName,
                                userHBFormat = hbFormat
                            )
                            insertSong(song)
                            Log.d(
                                "HomeScreen",
                                "Imported TXT as song: ${song.title} by ${song.artist}"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("HomeScreen", "Failed to import $uri", e)
                    }
                }
            }
        }
    }

    fun exportSongAsTxt(uri: Uri?, song: Song?, context: Context) {
        if (uri != null && song != null) {
            val content = TxtSongIO.songToTxtContent(song)
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
            }
            Log.d("SongScreen", "Exported song to TXT: $uri")
        }
    }
    //------------------------------- Playlist  operations ---------------------------------------------

    val _playlists: StateFlow<List<PlaylistEntity>> = playlistRepository.getAllPlaylists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    //playlist with songs count
    @OptIn(ExperimentalCoroutinesApi::class)
    val playlists: StateFlow<List<PlaylistInfo>> = _playlists
        .flatMapLatest { playlists ->
            if (playlists.isEmpty()) {
                flowOf(emptyList())
            } else {
                val infoFlows = playlists.map { playlist ->
                    playlistRepository.getSongsInPlaylist(playlist.id)
                        .map { songs -> PlaylistInfo(playlist = playlist, songCount = songs.size) }
                }
                combine(infoFlows) { array -> array.toList() }
            }
        }.stateIn(
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
            playlistRepository.deletePlaylist(_playlists.value.first { it.id == id })
        }
    }

    private val _playlistSongs = MutableStateFlow<List<Song>>(emptyList())
    val playlistSongs: StateFlow<List<Song>> = _playlistSongs.asStateFlow()
    private var playlistJob: Job? = null

    fun getSongsInPlaylist(playlistId: Int) {
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
            playlistRepository.getSongsInPlaylist(playlistId).collect { list ->
                _playlistSongs.value = list
            }
        }
    }

    fun getPlaylistById(id: Int): StateFlow<PlaylistEntity?> =
        _playlists.combine(_playlists) { playlists, _ ->
            playlists.firstOrNull { it.id == id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun moveSongInPlaylist(
        playlistId: Int,
        fromIndex: Int,
        toIndex: Int
    ) {
        val currentSongs = _playlistSongs.value.toMutableList()
        if (fromIndex in currentSongs.indices && toIndex in currentSongs.indices) {
            val song = currentSongs.removeAt(fromIndex)
            currentSongs.add(toIndex, song)
            _playlistSongs.value = currentSongs
            savePlaylistOrder(playlistId, currentSongs)
        }
    }


    fun savePlaylistOrder(playlistId: Int, orderedSongs: List<Song>) {
        viewModelScope.launch {
            val orderedSongIds = orderedSongs.mapNotNull { it.localId }
            playlistRepository.savePlaylistOrder(playlistId, orderedSongIds)
        }
    }

    fun removeSongFromPlaylist(playlistId: Int, songId: Int) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylistAndReorder(playlistId, songId)
        }
    }

    fun renamePlaylist(playlistId: Int, newName: String) {
        viewModelScope.launch {
            val playlist = _playlists.value.firstOrNull { it.id == playlistId }
            if (playlist != null) {
                val updated = playlist.copy(name = newName)
                playlistRepository.updatePlaylist(updated)
            }
        }
    }

    fun isNotFirstSongInPlaylist(playlistId: Int, songId: Int): Boolean {
        val songsInPlaylist = _playlistSongs.value
        val index = songsInPlaylist.indexOfFirst { it.localId == songId }
        return index > 0
    }

    fun isNotLastSongInPlaylist(playlistId: Int, songId: Int): Boolean {
        val songsInPlaylist = _playlistSongs.value
        val index = songsInPlaylist.indexOfFirst { it.localId == songId }
        return index >= 0 && index < songsInPlaylist.size - 1
    }

    fun navigateInsidePlaylist(
        navController: NavController,
        playlistId: Int,
        currentSongId: Int,
        direction: Int
    ) {
        val songsInPlaylist = _playlistSongs.value
        val currentIndex = songsInPlaylist.indexOfFirst { it.localId == currentSongId }
        val newIndex = currentIndex + direction
        if (newIndex in songsInPlaylist.indices) {
            val newSong = songsInPlaylist[newIndex]
            navController.navigate("songFromPlaylist/${newSong.localId}/$playlistId")
        }
    }

    fun positionInPlaylist(playlistId: Int, songId: Int): Int? {
        val songsInPlaylist = _playlistSongs.value
        val index = songsInPlaylist.indexOfFirst { it.localId == songId }
        return if (index >= 0) index + 1 else null
    }

    fun playlistSize(playlistId: Int): Int {
        return _playlistSongs.value.size
    }

    private val _seenNotifications = MutableStateFlow<Set<String>>(emptySet())

    private val _unseenNotifications = MutableStateFlow<List<NotificationDto>>(emptyList())
    val unseenNotifications: StateFlow<List<NotificationDto>> = _unseenNotifications.asStateFlow()

    private suspend fun loadSeenNotifications(): Set<String> {
        return notificationDataStore.getSeenNotificationIds().first()
    }

    fun getNotifications() {
        viewModelScope.launch {
            val seenIds = loadSeenNotifications()
            _seenNotifications.value = seenIds
            val result = songRemoteRepository.getNotifications()
            if (result.isSuccess) {
                val appVersion = appVersion.versionCode
                Log.d("MainViewModel", "Fetched notifications: ${result.getOrNull()}")
                Log.d("MainViewModel", "Seen notification IDs: $seenIds")
                Log.d("MainViewModel", "App version code: $appVersion")
                val notifications = result.getOrNull().orEmpty()
                val seenIds = _seenNotifications.value
                _unseenNotifications.value = notifications
                    .filterNot { seenIds.contains(it.id) }
                    .filter { appVersion in it.minVersion..it.maxVersion }
                Log.d("MainViewModel", "Unseen notifications after filtering: ${_unseenNotifications.value}")

            } else {
                Log.e("MainViewModel", "Failed to fetch notifications: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun setNotificationSeen(notification: NotificationDto) {
        viewModelScope.launch {
            notificationDataStore.markNotificationAsSeen(notification.id)
            _seenNotifications.value += notification.id
                _unseenNotifications.value = _unseenNotifications.value.filterNot { it.id == notification.id }
        }
    }

}








