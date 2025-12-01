package com.chordbay.app.data.database.playlist

import androidx.room.*
import com.chordbay.app.data.database.song.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query(
        "DELETE FROM playlists_song_cross_ref " +
                "WHERE playlistId = :playlistId AND song_id_in_cross_ref = :songId"
    )
    suspend fun removeSongFromPlaylist(playlistId: Int, songId: Int)

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistWithSongs(playlistId: Int): Flow<PlaylistWithSongs?>

    @Transaction
    @Query(
        """
        SELECT * FROM playlists_song_cross_ref
        WHERE playlistId = :playlistId
        ORDER BY 
            CASE
                WHEN position IS NULL THEN 0
                ELSE position
            END ASC
        """
    )
    suspend fun getCrossRefsForPlaylistOrdered(playlistId: Int): List<PlaylistSongCrossRef>

    @Query("SELECT * FROM playlists_song_cross_ref WHERE playlistId = :playlistId")
    suspend fun getCrossRefsForPlaylist(playlistId: Int): List<PlaylistSongCrossRef>


    @Update
    suspend fun updateCrossRef(crossRef: PlaylistSongCrossRef)
    @Update
    suspend fun updateCrossRefs(crossRefs: List<PlaylistSongCrossRef>)


    @Query(
        "UPDATE playlists_song_cross_ref " +
                "SET position = :position " +
                "WHERE playlistId = :playlistId AND song_id_in_cross_ref = :localId"
    )
    suspend fun updateCrossRefPosition(playlistId: Int, localId: Int, position: Int)

    @Transaction
    suspend fun reorderPlaylistSongs(playlistId: Int, orderedLocalIds: List<Int>) {
        if (orderedLocalIds.isEmpty()) return
        // Ensure we only update rows that actually exist for this playlist
        val existing = getCrossRefsForPlaylistOrdered(playlistId).map { it.songId } // adjust name to property in entity
        orderedLocalIds.forEachIndexed { index, localId ->
            if (existing.contains(localId)) {
                updateCrossRefPosition(playlistId, localId, index)
            }
        }
    }


    @Transaction
    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query(
        """
    SELECT s.* FROM songs s
    INNER JOIN playlists_song_cross_ref c
        ON s.id = c.song_id_in_cross_ref
    WHERE c.playlistId = :playlistId
    ORDER BY
        CASE
            WHEN c.position IS NULL THEN 0
            ELSE c.position
        END ASC
    """
    )
    fun getSongsInPlaylistOrdered(playlistId: Int): Flow<List<SongEntity>>
}
