package com.example.chords2.di

import androidx.room.Room
import com.example.chords2.data.database.AppDatabase
import com.example.chords2.data.datastore.SettingsDataStore
import com.example.chords2.data.remote.RetrofitInstance
import com.example.chords2.data.repository.PostRepository
import com.example.chords2.data.repository.PostRepositoryImpl
import com.example.chords2.data.repository.SongRepository
import com.example.chords2.data.repository.SongRepositoryImpl
import com.example.chords2.ui.viewmodel.SongViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SongRepository> { SongRepositoryImpl(get()) }
    viewModel { SongViewModel(get(), get(), get()) }
    single<CoroutineScope> { CoroutineScope(Dispatchers.IO) }
    single {
        AppDatabase.getDatabase(androidContext(), get())
    }
    single {
        get<AppDatabase>().songDao()
    }

    // jsonplaceholder api
    single {
        RetrofitInstance.api
    }
    single<PostRepository> {
        PostRepositoryImpl(get())
    }
    single { SettingsDataStore(androidContext()) }
}
