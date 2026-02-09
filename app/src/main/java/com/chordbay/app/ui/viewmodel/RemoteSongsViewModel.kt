package com.chordbay.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.chordbay.app.data.repository.remote.SongRemoteRepository
import com.chordbay.app.data.repository.song.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.chordbay.app.data.model.Song
import com.chordbay.app.data.model.util.SortBy
import com.chordbay.app.data.model.util.toError
import com.chordbay.app.data.remote.model.ArtistDto
import com.chordbay.app.ui.composable.screen.song.FilterField
import com.chordbay.app.ui.composable.screen.song.ResultMode
import com.chordbay.app.ui.composable.screen.song.SortByArtist
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RemoteSongsViewModel(
    private val songRemoteRepository: SongRemoteRepository,
    private val songRepository: SongRepository
) : ViewModel() {
    // UI state
    val query = MutableStateFlow("")
    val field = MutableStateFlow(FilterField.BOTH)
    val sortSongs = MutableStateFlow(SortBy.SONG_NAME)
    val sortArtists = MutableStateFlow(SortByArtist.ALPHABETICAL)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val artistFilterQuery = MutableStateFlow("")
    private val _artists = MutableStateFlow<List<ArtistDto>>(emptyList())

    val artistFirstLetters = _artists.map { artists ->
        artists.mapNotNull { it.name.firstOrNull()?.uppercaseChar() }.toSet().sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val artistFirstLetterFilterChipSelected = MutableStateFlow<Char?>(null)
    fun onArtistFirstLetterFilterChange(letter: Char?) {
        artistFirstLetterFilterChipSelected.value = letter
        Log.d("RemoteSongsViewModel", "First letter filter changed to: $letter")
    }

    val artists: StateFlow<List<ArtistDto>> = combine(
        _artists,
        artistFilterQuery,
        artistFirstLetterFilterChipSelected,
        sortArtists
    ) { artists, filterQuery, charChip, sort ->
        // first apply the chip (if any), then apply the text query (if any)
        val chipFiltered = if (charChip == null) {
            artists
        } else {
            val prefix = charChip.toString()
            artists.filter { artist ->
                artist.name.startsWith(prefix, ignoreCase = true)
            }
        }
        if (filterQuery.isBlank()) {
            chipFiltered.sortedWith(
                when (sort) {
                    SortByArtist.ALPHABETICAL -> compareBy { it.name.lowercase() }
                    SortByArtist.MOST_SONGS -> compareByDescending<ArtistDto> { it.songCount }
                        .thenBy { it.name.lowercase() }
                }
            )
        } else {
            chipFiltered.filter { artist ->
                artist.name.contains(filterQuery, ignoreCase = true)
            }.sortedWith(
                when (sort) {
                    SortByArtist.ALPHABETICAL -> compareBy { it.name.lowercase() }
                    SortByArtist.MOST_SONGS -> compareByDescending<ArtistDto> { it.songCount }
                        .thenBy { it.name.lowercase() }
                }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val showMostViewed = MutableStateFlow(true)
    fun onShowMostViewedClick() {
        showMostViewed.value = !showMostViewed.value
        if (showMostViewed.value) {
            getMostViewedSongs()
        }else{
            searchDebounced()
        }
    }

    private val _songsRaw = MutableStateFlow<List<Song>>(emptyList())

    private val _searchOption = MutableStateFlow<ResultMode>(ResultMode.ARTISTS)
    val searchOption: StateFlow<ResultMode> = _searchOption.asStateFlow()
    fun onSearchOptionChange(newOption: ResultMode) {
        _searchOption.value = newOption
        onQueryChanged(query.value)
        if (newOption == ResultMode.ARTISTS) {
            refreshArtists()
        } else if (newOption == ResultMode.SONGS && query.value.isNotBlank()) {
            searchDebounced()
        } else if (newOption == ResultMode.SONGS && showMostViewed.value == true) {
            getMostViewedSongs()
        }
    }

    // Local set of remote IDs that already exist in your local DB
    private val localRemoteIds: StateFlow<Set<String>> =
        songRepository.getAllSongs()
            .map { list -> list.mapNotNull { it.remoteId }.toSet() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // Songs with markSynced flagged
    private val songsMarked: StateFlow<List<Song>> =
        combine(
            _songsRaw,
            localRemoteIds
        ) { songs, localIds ->
            songs.map { song ->
                if (song.remoteId != null && localIds.contains(song.remoteId)) {
                    song.copy(markSynced = true)
                } else {
                    song
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Client-side sorted songs for rendering
    val songs: StateFlow<List<Song>> =
        combine(
            songsMarked,
            sortSongs
        ) { songs, sortBy ->
            when (sortBy) {
                SortBy.SONG_NAME -> songs.sortedBy { it.title.lowercase() }
                SortBy.ARTIST_NAME -> songs.sortedBy { it.artist.lowercase() }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var searchJob: Job? = null

    init {
        refreshArtists()
    }

    fun onQueryChanged(newQuery: String) {
        query.value = newQuery
        if (newQuery.isBlank()) {
            _songsRaw.value = emptyList()
            _error.value = null
            artistFilterQuery.value = ""
            if (artists.value.isEmpty()) refreshArtists()
        } else {
            when (_searchOption.value) {
                ResultMode.SONGS -> {
                    if (showMostViewed.value) {
                        onShowMostViewedClick()
                    } else {
                        searchDebounced()
                    }
                }
                ResultMode.ARTISTS -> artistFilterQuery.value = newQuery
            }
        }
    }

    fun onFieldChanged(newField: FilterField) {
        field.value = newField
        if (query.value.isNotBlank()) search()
    }

    fun onSortChanged(sortOption: ResultMode, songSort: SortBy? = null, artistSort: SortByArtist? = null) {
        when (sortOption) {
            ResultMode.SONGS -> {
                if (songSort != null) {
                    if (sortSongs.value != songSort) {
                        sortSongs.value = songSort
                    }
                }
            }
            ResultMode.ARTISTS -> {
                if (artistSort != null) {
                    if (sortArtists.value != artistSort) {
                        sortArtists.value = artistSort
                    }
                }
            }
        }
    }

    fun refreshArtists() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            songRemoteRepository.getAllArtists()
                .onSuccess { _artists.value = it }
                .onFailure { _error.value = it.message?.toError()?.message }
            _loading.value = false
        }
    }

    private fun searchDebounced() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            search()
        }
    }

    fun search() {
        val q = query.value
        if (q.isBlank() && !showMostViewed.value) {
            _songsRaw.value = emptyList()
            return
        }
        if (q.isBlank()) return
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            songRemoteRepository.searchSongs(
                query = q,
                field = field.value,
                offset = 0,
                limit = 50
            )
                .onSuccess { _songsRaw.value = it }
                .onFailure { _error.value = it.message?.toError()?.message }
            _loading.value = false
        }
    }

    private val _saveSuccess = MutableStateFlow<Boolean?>(null)
    val saveSuccess = _saveSuccess.asStateFlow()
    fun clearSaveSuccess() {
        _saveSuccess.value = null
    }

    fun saveSong(song: Song) {
        viewModelScope.launch {
            if (song.remoteId != null) {
                val localIds = localRemoteIds.value
                if (localIds.contains(song.remoteId)) {
                    _artistSongs.value.map {
                        if (it.remoteId == song.remoteId) {
                            it.copy(markSynced = true)
                        } else {
                            it
                        }
                    }
                } else {
                    _saveSuccess.value = true
                    songRepository.insertRemoteSong(song)
                }
            }
        }
    }

    private val _artistSongs = MutableStateFlow<List<Song>>(emptyList())
    val artistSongs =
        combine(
            _artistSongs,
            localRemoteIds,
        ) { songs, localIds ->
            songs.map { song ->
                if (song.remoteId != null && localIds.contains(song.remoteId)) {
                    song.copy(markSynced = true)
                } else {
                    song
                }
            }
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    fun getSongsByArtist(artist: String) {
        Log.d("SongViewModel", "getSongsByArtist called with artist: $artist")
        viewModelScope.launch {
            songRemoteRepository.getSongsByArtist(artist)
                .onSuccess { fetchedSongs ->
                    _artistSongs.value = fetchedSongs
                    Log.d("SongViewModel", "Fetched ${fetchedSongs.size} songs for artist: $artist")
                }
                .onFailure { exception ->
                    _error.value = "Failed to fetch songs by artist: ${exception.message}".toError().message
                    Log.e(
                        "SongViewModel",
                        "Error fetching songs for artist $artist: ${exception.message}"
                    )
                }
        }
    }

    fun saveAllSongs(songs: List<Song>) {
        for (song in songs) {
            saveSong(song)
        }
    }

    fun clearError(){
        _error.value = null
    }

    private fun getMostViewedSongs() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _songsRaw.value = emptyList()
            songRemoteRepository.getSongsByViewedCount()
                .onSuccess { _songsRaw.value = it }
                .onFailure { _error.value = it.message?.toError()?.message }
            _loading.value = false
        }
    }
}