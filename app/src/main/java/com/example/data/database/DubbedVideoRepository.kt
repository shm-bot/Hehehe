package com.example.data.database

import kotlinx.coroutines.flow.Flow

class DubbedVideoRepository(private val dubbedVideoDao: DubbedVideoDao) {
    val allVideos: Flow<List<DubbedVideo>> = dubbedVideoDao.getAllVideos()

    fun getVideoById(id: Int): Flow<DubbedVideo?> {
        return dubbedVideoDao.getVideoById(id)
    }

    suspend fun insertVideo(video: DubbedVideo): Long {
        return dubbedVideoDao.insertVideo(video)
    }

    suspend fun updateVideo(video: DubbedVideo) {
        dubbedVideoDao.updateVideo(video)
    }

    suspend fun deleteVideo(video: DubbedVideo) {
        dubbedVideoDao.deleteVideo(video)
    }

    suspend fun deleteVideoById(id: Int) {
        dubbedVideoDao.deleteVideoById(id)
    }
}
