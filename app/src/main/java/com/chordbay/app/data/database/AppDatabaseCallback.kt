package com.chordbay.app.data.database

import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chordbay.app.data.TestData
import com.chordbay.app.data.database.song.SongDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppDatabaseCallback(
    private val scope: CoroutineScope
): RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
            scope.launch {
                populateDatabase(database.songDao())
            }
        }
    }
    private suspend fun populateDatabase(songDao: SongDao) {
        for(song in TestData.songs) {
            Log.d("AppDatabaseCallback", "Adding song: $song")
            songDao.insertSong(song)
        }
    }

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null
    }
}