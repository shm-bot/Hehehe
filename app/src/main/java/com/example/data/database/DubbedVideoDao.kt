package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DubbedVideoDao {
    @Query("SELECT * FROM dubbed_videos ORDER BY timestamp DESC")
    fun getAllVideos(): Flow<List<DubbedVideo>>

    @Query("SELECT * FROM dubbed_videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: Int): Flow<DubbedVideo?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: DubbedVideo): Long

    @Update
    suspend fun updateVideo(video: DubbedVideo)

    @Delete
    suspend fun deleteVideo(video: DubbedVideo)

    @Query("DELETE FROM dubbed_videos WHERE id = :id")
    suspend fun deleteVideoById(id: Int)
}
