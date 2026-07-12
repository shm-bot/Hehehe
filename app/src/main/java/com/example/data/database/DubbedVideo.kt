package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dubbed_videos")
data class DubbedVideo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val source: String, // URL or "file_name.mp4"
    val sourceType: String, // "URL" or "FILE"
    val duration: String, // e.g., "01:24"
    val targetLanguage: String, // e.g., "العربية", "English"
    val voiceName: String, // Selected voice setting
    val subtitlesJson: String, // Custom serialized subtitles list
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED" // "PROCESSING", "COMPLETED", "FAILED"
)

data class SubtitleItem(
    val id: Int,
    val start: String, // "00:01"
    val end: String, // "00:05"
    val originalText: String,
    val translatedText: String
)

object SubtitleSerializer {
    fun serializeSubtitles(items: List<SubtitleItem>): String {
        return items.joinToString(";;") { "${it.id}||${it.start}||${it.end}||${it.originalText}||${it.translatedText}" }
    }

    fun deserializeSubtitles(data: String?): List<SubtitleItem> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            data.split(";;").mapNotNull { item ->
                val parts = item.split("||")
                if (parts.size >= 5) {
                    SubtitleItem(
                        id = parts[0].toIntOrNull() ?: 0,
                        start = parts[1],
                        end = parts[2],
                        originalText = parts[3],
                        translatedText = parts[4]
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
