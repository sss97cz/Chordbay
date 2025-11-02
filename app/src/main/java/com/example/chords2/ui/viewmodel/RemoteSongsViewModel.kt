package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chords2.data.repository.remote.SongRemoteRepository
import com.example.chords2.data.repository.song.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.remote.model.ArtistDto
import com.example.chords2.ui.composable.screen.FilterField
import com.example.chords2.ui.composable.screen.ResultMode
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
    val sort = MutableStateFlow(SortBy.SONG_NAME)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val artistFilterQuery = MutableStateFlow("")
    private val _artists = MutableStateFlow<List<ArtistDto>>(emptyList())
    val artists: StateFlow<List<ArtistDto>> = combine(
        _artists,
        artistFilterQuery
    ) { artists, filterQuery ->
        if (filterQuery.isBlank()) {
            artists
        } else {
            artists.filter { artist ->
                artist.name.contains(filterQuery, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _songsRaw = MutableStateFlow<List<Song>>(emptyList())

    private val _searchOption = MutableStateFlow<ResultMode>(ResultMode.SONGS)
    val searchOption: StateFlow<ResultMode> = _searchOption.asStateFlow()
    fun onSearchOptionChange(newOption: ResultMode) {
        _searchOption.value = newOption
        onQueryChanged(query.value)
        if (newOption == ResultMode.ARTISTS) {
            refreshArtists()
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
            sort
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
                ResultMode.SONGS -> searchDebounced()
                ResultMode.ARTISTS -> artistFilterQuery.value = newQuery
            }
        }
    }

    fun onFieldChanged(newField: FilterField) {
        field.value = newField
        if (query.value.isNotBlank()) search()
    }

    fun onSortChanged(newSort: SortBy) {
        sort.value = newSort
    }

    fun refreshArtists() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            songRemoteRepository.getAllArtists()
                .onSuccess { _artists.value = it }
                .onFailure { _error.value = it.message }
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
                .onFailure { _error.value = it.message }
            _loading.value = false
        }
    }

    fun saveSong(song: Song) {
        viewModelScope.launch {
            // Insert into local DB; the localRemoteIds flow will update and mark the item as synced
            if (song.remoteId != null){
                val localIds = localRemoteIds.value
                if (localIds.contains(song.remoteId)) {
                    _artistSongs.value.map {
                        if (it.remoteId == song.remoteId) {
                            it.copy(markSynced = true)
                        } else {
                            it
                        }
                    }
                }
            }
            songRepository.insertRemoteSong(song)
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
                    _error.value = "Failed to fetch songs by artist: ${exception.message}"
                    Log.e(
                        "SongViewModel",
                        "Error fetching songs for artist $artist: ${exception.message}"
                    )
                }
        }
    }

    private fun filterArtists(query: String) {
        artistFilterQuery.value = query
    }
}