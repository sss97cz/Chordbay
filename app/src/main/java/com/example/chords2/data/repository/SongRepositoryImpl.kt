package com.example.chords2.data.repository

import android.util.Log
import com.example.chords2.data.database.SongDao
import com.example.chords2.data.database.SongEntity
import com.example.chords2.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class SongRepositoryImpl(
    private val songDao: SongDao
) : SongRepository {
//    private val _songsList: MutableStateFlow<List<Song>> = MutableStateFlow(
//        List(10) {
//            Song(
//                it.toString(), content = """Mělo [G]český království
//ve všech městech to se ví
//svoje pivovary každý střežil
//[D]svoje tajemství.
//Slaný bylo město měst
//křižovatkou hlavních cest
//léta páně roku
//jeden tisíc [G]pětset třicet šest.
//
//
//Měšťan Antoš za to vzal
//svoje pivo vařit dal
//mezi domy s právem
//várečným byl
//zdejší pivní král.
//Tenhle věhlas přetrval
//v knihách starých o tom psal
//slavný jezuita Balbín
//co to sdílel v Čechách dál.
//
//Ref:
//[Em]Je to pivo z města Slaný
//[C]je to pivo požehnaný
//[G]je to pivo co je víc
//než co je [D]pocit rozkoše
//[Em]je to pivo co je živý
//[C]je to pivo mezi pivy
//[G]je to naše slánský pivo
//z pivo[D]varu Antoše.
//
//Jenže pak zákazy shora
//Martinic a Bílá Hora
//přesto pivo vařil sládek Poupě
//byl tu za nestora
//nastal ale s vodou svízel
//náš pivovar s krizí zmizel
//a pak sto pětatřicet let
//slánský pivo nenabízel.
//
//Až pak v dalším století
//prolomili prokletí
//slánský Antoš znovu ožil
//s novou pivní pečetí.
//Žádnej obyčejnej bar
//ale slánskej pivovar
//tam je to nejlepší pivo
//Antoš česká pivní stár.
//
//3x Ref.""".trimIndent()
//            )
//        })
//
//    val songsList: StateFlow<List<Song>> = _songsList
//
//    override fun getAllSongs(): Flow<List<Song>> = songsList
//
//
//    override fun getSongById(id: String): Flow<Song?> =
//        _songsList.map { songs ->
//            songs.firstOrNull { it.songId == id }
//        }
//
//    override fun updateSong(song: Song): Boolean {
//        val index = _songsList.value.indexOfFirst { it.songId == song.songId }
//        return if (index == -1) {
//            false
//        } else {
//            val updatedList = _songsList.value.toMutableList()
//            updatedList[index] = song
//            _songsList.value = updatedList
//            true
//        }
//    }
//
//    override suspend fun insertSong(song: Song): Boolean {
//        _songsList.value += song
//        return true
//    }
//
//
//    override suspend fun deleteSong(song: Song): Boolean {
//        return if (!_songsList.value.contains(song)) {
//            false
//        } else {
//            _songsList.value -= song
//            true
//        }
//    }

    override fun getAllSongs(): Flow<List<SongEntity>> = songDao.getAllSongs()

    override fun getSongById(id: Int): Flow<SongEntity?> = songDao.getSongById(id)

    override suspend fun insertSong(song: SongEntity) =
        songDao.insertSong(song)


    override suspend fun updateSong(song: SongEntity) {
        songDao.updateSong(song)
    }

    override suspend fun deleteSong(song: SongEntity) {
        songDao.deleteSong(song)
    }
}