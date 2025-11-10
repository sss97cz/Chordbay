package com.example.chords2.di

import com.example.chords2.data.database.AppDatabase
import com.example.chords2.data.datastore.CredentialManager
import com.example.chords2.data.datastore.SettingsDataStore
import com.example.chords2.data.datastore.UserDataStore
import com.example.chords2.data.remote.RetrofitInstance
import com.example.chords2.data.repository.auth.AuthRepository
import com.example.chords2.data.repository.auth.AuthRepositoryImpl
import com.example.chords2.data.repository.playlist.PlaylistRepository
import com.example.chords2.data.repository.playlist.PlaylistRepositoryImpl
import com.example.chords2.data.repository.remote.SongRemoteRepository
import com.example.chords2.data.repository.remote.SongRemoteRepositoryImpl
import com.example.chords2.data.repository.song.SongRepository
import com.example.chords2.data.repository.song.SongRepositoryImpl
import com.example.chords2.ui.viewmodel.AuthViewModel
import com.example.chords2.ui.viewmodel.EditViewModel
import com.example.chords2.ui.viewmodel.RemoteSongsViewModel
import com.example.chords2.ui.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SongRepository> { SongRepositoryImpl(get()) }
    viewModel {
        MainViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    viewModel {
        AuthViewModel(
            get(),
            get(),
            get()
        )
    }
    viewModel {
        RemoteSongsViewModel(
            get(),
            get(),
        )
    }
    viewModel {
        EditViewModel(
            get(),
        )
    }
    single<CoroutineScope> { CoroutineScope(Dispatchers.IO) }
    single {
        AppDatabase.getDatabase(androidContext(), get())
    }
    single {
        get<AppDatabase>().songDao()
    }
    single {
        get<AppDatabase>().playlistDao()
    }

    single {
        RetrofitInstance.api
    }
    single {
        RetrofitInstance.authApiService
    }
    single<SongRemoteRepository> {
        SongRemoteRepositoryImpl(get())
    }
    single { SettingsDataStore(androidContext()) }

    single { UserDataStore(androidContext()) }

    single { CredentialManager(androidContext()) }

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }
}
