package com.example.chords2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.util.Chords
import com.example.chords2.data.model.util.MainTabs
import com.example.chords2.data.model.post.Post
import com.example.chords2.data.model.Song
import com.example.chords2.data.model.util.Settings
import com.example.chords2.data.model.util.SortBy
import com.example.chords2.data.model.util.ThemeMode
import com.example.chords2.data.repository.PostRepository
import com.example.chords2.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SongViewModel(
    private val songRepository: SongRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _sortOption = MutableStateFlow(SortBy.SONG_NAME)
    val sortOption: StateFlow<SortBy> = _sortOption.asStateFlow()
    fun setSortOption(sortOption: SortBy) {
        Log.d("SongViewModel", "Setting sort option to: $sortOption")
        _sortOption.value = sortOption
    }
    private val _searchQuery = MutableStateFlow("")
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private val _selectedTab = MutableStateFlow(MainTabs.MY_SONGS)
    val selectedTab: StateFlow<MainTabs> = _selectedTab.asStateFlow() // Expose as immutable StateFlow
    fun selectTab(tab: MainTabs) {
        _selectedTab.value = tab
        // You could add other logic here if needed
    }
    private val _songTextFontSize = MutableStateFlow(Settings.FontSize.defaultValue)
    val songTextFontSize: StateFlow<Int> = _songTextFontSize.asStateFlow()
    fun setSongTextFontSize(fontSize: Int) {
        _songTextFontSize.value = fontSize
    }

    private val _themeMode: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()
    fun setThemeMode(themeMode: ThemeMode){
        _themeMode.value = themeMode
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

    // jsonplaceholder api
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            postRepository.getPosts()
                .onSuccess { fetchedPosts ->
                    _posts.value = fetchedPosts
                }
                .onFailure { exception ->
                    _error.value = "Failed to fetch posts: ${exception.message}"
                }
            _isLoading.value = false
        }
    }

    fun submitNewPost(post: Post) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            postRepository.createPost(post = post)
                .onSuccess { createdPost ->
                    // Successfully created, you might want to refresh the list
                    // or add it to the existing list to update UI immediately
                    fetchPosts() // Simplest way to refresh
                    // Or: _posts.value = _posts.value + createdPost
                    _error.value = "Post created: ${createdPost.title}" // User feedback
                }
                .onFailure { exception ->
                    _error.value = "Failed to create post: ${exception.message}"
                }
            _isLoading.value = false
        }
    }

    fun savePostToDb(post: Post) {
        viewModelScope.launch {
            val song = SongEntity(
                title = post.title,
                artist = post.userId.toString(),
                content = post.body
            )
            songRepository.insertSong(song)
        }
    }

}