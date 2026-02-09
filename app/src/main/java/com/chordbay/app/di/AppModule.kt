package com.chordbay.app.di

import com.chordbay.app.data.database.AppDatabase
import com.chordbay.app.data.datastore.CredentialManager
import com.chordbay.app.data.datastore.FirstLaunchDatastore
import com.chordbay.app.data.datastore.NotificationDataStore
import com.chordbay.app.data.datastore.SettingsDataStore
import com.chordbay.app.data.datastore.UserDataStore
import com.chordbay.app.data.helper.AppVersion
import com.chordbay.app.data.remote.RetrofitInstance
import com.chordbay.app.data.repository.auth.AuthRepository
import com.chordbay.app.data.repository.auth.AuthRepositoryImpl
import com.chordbay.app.data.repository.playlist.PlaylistRepository
import com.chordbay.app.data.repository.playlist.PlaylistRepositoryImpl
import com.chordbay.app.data.repository.remote.SongRemoteRepository
import com.chordbay.app.data.repository.remote.SongRemoteRepositoryImpl
import com.chordbay.app.data.repository.song.SongRepository
import com.chordbay.app.data.repository.song.SongRepositoryImpl
import com.chordbay.app.ui.viewmodel.AuthViewModel
import com.chordbay.app.ui.viewmodel.EditViewModel
import com.chordbay.app.ui.viewmodel.RemoteSongsViewModel
import com.chordbay.app.ui.viewmodel.MainViewModel
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
            get()
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

    single { NotificationDataStore(androidContext()) }

    single { CredentialManager(androidContext()) }

    single { FirstLaunchDatastore(androidContext()) }

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get())
    }
    single<AppVersion> {
        AppVersion(androidContext())
    }
}
