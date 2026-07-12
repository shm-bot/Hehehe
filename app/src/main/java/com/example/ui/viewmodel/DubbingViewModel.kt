package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiDubbingService
import com.example.data.database.AppDatabase
import com.example.data.database.DubbedVideo
import com.example.data.database.DubbedVideoRepository
import com.example.data.database.SubtitleItem
import com.example.data.database.SubtitleSerializer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    EXPLORE, PLAYER, HISTORY
}

enum class SourceType {
    URL, FILE
}

data class DubbingSegment(
    val id: Int,
    val timeRange: String,
    val status: String // "QUEUED", "PROCESSING", "READY"
)

class DubbingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DubbedVideoRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DubbedVideoRepository(database.dubbedVideoDao())
    }

    // Tab state
    var currentTab by mutableStateOf(AppTab.EXPLORE)

    // Inputs
    var urlInput by mutableStateOf("")
    var selectedSourceType by mutableStateOf(SourceType.URL)
    var uploadedFileName by mutableStateOf("")
    var selectedLanguage by mutableStateOf("العربية (مصر)")
    var selectedVoice by mutableStateOf("Original Voice Clone (استنساخ الصوت الأصلي ✦)")
    
    // Toggles
    var turboModeEnabled by mutableStateOf(true)
    var lipSyncEnabled by mutableStateOf(true)
    var noiseRemovalEnabled by mutableStateOf(true)

    // Ultra Performance & Free-Tier Mode
    var exportResolution by mutableStateOf("2K (1440p) Ultra HD ✦")
    var frameRateSmoothness by mutableStateOf("120 FPS Extreme Smoothness ✨")
    var isTotallyFreeUnlimited by mutableStateOf(true)

    // DB list
    val historyList: StateFlow<List<DubbedVideo>> = repository.allVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active playing video
    var activeDubbedVideo by mutableStateOf<DubbedVideo?>(null)
    var activeSubtitlesList by mutableStateOf<List<SubtitleItem>>(emptyList())
    var currentSubtitleIndex by mutableStateOf(-1)

    // Processing state
    var isProcessing by mutableStateOf(false)
    var processingProgress by mutableStateOf(0f)
    var processingStep by mutableStateOf("")
    var currentDubbingSegments by mutableStateOf<List<DubbingSegment>>(emptyList())

    // Player state
    var isPlaying by mutableStateOf(false)
    var currentPlaybackTime by mutableStateOf(0f) // seconds, 0..25s
    var selectedAudioTrack by mutableStateOf("DUBBED") // "DUBBED" or "ORIGINAL"
    private var playbackJob: kotlinx.coroutines.Job? = null
    
    // Error state
    var errorMessage by mutableStateOf<String?>(null)

    // Presets/Samples for simple demo/upload
    val sampleVideoLinks = listOf(
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
        "https://tiktok.com/@aitech/video/1792837",
        "https://instagram.com/p/C-28djhskA"
    )

    val sampleLocalFiles = listOf(
        "ai_future_speech.mp4",
        "nature_documentary_clip.mp3",
        "tech_keynote_segment.mov"
    )

    val availableLanguages = listOf(
        "العربية (العراق)",
        "العربية (مصر)",
        "العربية (السعودية)",
        "العربية (سوريا)",
        "English (US)",
        "English (UK)",
        "Español (España)",
        "Español (México)",
        "Français (France)",
        "Deutsch (Deutschland)",
        "Türkçe (Türkiye)",
        "Italiano (Italia)",
        "日本語 (Japan)",
        "Русский (Russia)",
        "中文 (China)",
        "هندي (India)"
    )

    val availableVoices = listOf(
        "Original Voice Clone (استنساخ الصوت الأصلي ✦)",
        "Samer - News Anchor (سامر - إخباري)",
        "Laila - Narrative Documentary (ليلى - سرد وثائقي)",
        "Youssef - Cinematic Voice (يوسف - سينمائي دافئ)",
        "Emily - Natural English (إيميلي - إنجليزي طبيعي)",
        "Sarah - Friendly Narrator (سارة - مبهجة)"
    )

    fun onUrlChange(newUrl: String) {
        urlInput = newUrl
        errorMessage = null
    }

    fun selectSourceType(type: SourceType) {
        selectedSourceType = type
        errorMessage = null
    }

    fun selectUploadedFile(fileName: String) {
        uploadedFileName = fileName
        errorMessage = null
    }

    fun selectLanguage(lang: String) {
        selectedLanguage = lang
    }

    fun selectVoice(voice: String) {
        selectedVoice = voice
    }

    // Start automated dubbing action
    fun startDubbingProcess() {
        val input = if (selectedSourceType == SourceType.URL) urlInput else uploadedFileName
        if (input.isBlank()) {
            errorMessage = "الرجاء إدخال رابط أو اختيار ملف فيديو/صوت للبدء."
            return
        }

        isProcessing = true
        processingProgress = 0f
        errorMessage = null

        // Initialize progressive rendering segments
        currentDubbingSegments = listOf(
            DubbingSegment(1, "00:00 - 00:06", "QUEUED"),
            DubbingSegment(2, "00:06 - 00:12", "QUEUED"),
            DubbingSegment(3, "00:12 - 00:18", "QUEUED"),
            DubbingSegment(4, "00:18 - 00:25", "QUEUED")
        )

        viewModelScope.launch {
            try {
                // Step 1: Call Gemini to generate context-aware translation and structured subtitles
                processingStep = "جاري تحليل المحتوى باستخدام الذكاء الاصطناعي..."
                processingProgress = 0.1f
                delay(1200)

                val result = GeminiDubbingService.analyzeAndDub(
                    inputSource = input,
                    sourceType = selectedSourceType.name,
                    targetLanguage = selectedLanguage,
                    voiceName = selectedVoice
                )

                // Step 2: Insert initial DB entry as processing
                val initialVideo = DubbedVideo(
                    title = result.title,
                    source = input,
                    sourceType = selectedSourceType.name,
                    duration = "00:25",
                    targetLanguage = result.targetLang,
                    voiceName = voiceNameArabic(selectedVoice),
                    subtitlesJson = SubtitleSerializer.serializeSubtitles(result.subtitles),
                    status = "PROCESSING"
                )

                val insertedId = repository.insertVideo(initialVideo).toInt()
                val currentVideoWithId = initialVideo.copy(id = insertedId)

                // Step 3: Segment-by-segment Turbo parallel processing simulation (as requested!)
                processingStep = "وضع الـ Turbo نشط: تقسيم الفيديو ومعالجة الأجزاء بالتوازي..."
                processingProgress = 0.25f
                delay(1500)

                // Segment 1
                updateSegmentStatus(1, "PROCESSING")
                processingStep = "جاري استخلاص الصوت وفصل المتحدثين للجزء الأول..."
                processingProgress = 0.4f
                delay(1500)
                updateSegmentStatus(1, "READY")

                // Segment 2
                updateSegmentStatus(2, "PROCESSING")
                processingStep = "استنساخ نبرة الصوت والمشاعر للجزء الثاني ومزامنة حركة الشفاه..."
                processingProgress = 0.6f
                delay(1500)
                updateSegmentStatus(2, "READY")

                // Segment 3
                updateSegmentStatus(3, "PROCESSING")
                processingStep = "تطبيق الفلترة الصوتية وإزالة الضوضاء للجزء الثالث..."
                processingProgress = 0.8f
                delay(1500)
                updateSegmentStatus(3, "READY")

                // Segment 4
                updateSegmentStatus(4, "PROCESSING")
                processingStep = "رندرة المشاهد النهائية ومطابقة الدقة للجزء الرابع..."
                processingProgress = 0.95f
                delay(1200)
                updateSegmentStatus(4, "READY")

                // Complete
                val completedVideo = currentVideoWithId.copy(status = "COMPLETED")
                repository.updateVideo(completedVideo)

                activeDubbedVideo = completedVideo
                activeSubtitlesList = result.subtitles
                currentPlaybackTime = 0f
                isProcessing = false
                
                // Navigate to Player Tab
                currentTab = AppTab.PLAYER
                playVideo()

            } catch (e: Exception) {
                errorMessage = "حدث خطأ أثناء عملية الدبلجة: ${e.message}"
                isProcessing = false
            }
        }
    }

    private fun updateSegmentStatus(id: Int, status: String) {
        currentDubbingSegments = currentDubbingSegments.map {
            if (it.id == id) it.copy(status = status) else it
        }
    }

    private fun voiceNameArabic(voiceName: String): String {
        return voiceName.substringBefore(" (").trim()
    }

    // Playback control
    fun playVideo() {
        if (isPlaying && playbackJob?.isActive == true) return
        isPlaying = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            while (isPlaying && currentPlaybackTime < 25f) {
                delay(100)
                currentPlaybackTime += 0.1f
                updateSubtitleIndex()
            }
            if (currentPlaybackTime >= 25f) {
                isPlaying = false
                currentPlaybackTime = 0f
                currentSubtitleIndex = -1
            }
        }
    }

    fun pauseVideo() {
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun seekTo(time: Float) {
        currentPlaybackTime = time.coerceIn(0f, 25f)
        updateSubtitleIndex()
    }

    private fun updateSubtitleIndex() {
        val timeStr = formatPlaybackTime(currentPlaybackTime)
        currentSubtitleIndex = activeSubtitlesList.indexOfFirst { item ->
            val startSec = timeToSeconds(item.start)
            val endSec = timeToSeconds(item.end)
            currentPlaybackTime >= startSec && currentPlaybackTime <= endSec
        }
    }

    private fun timeToSeconds(timeStr: String): Float {
        val parts = timeStr.split(":")
        return if (parts.size >= 2) {
            val min = parts[0].toIntOrNull() ?: 0
            val sec = parts[1].toIntOrNull() ?: 0
            (min * 60 + sec).toFloat()
        } else 0f
    }

    fun formatPlaybackTime(sec: Float): String {
        val m = (sec / 60).toInt()
        val s = (sec % 60).toInt()
        return String.format("%02d:%02d", m, s)
    }

    // Switch active project from History
    fun selectActiveVideoFromHistory(video: DubbedVideo) {
        activeDubbedVideo = video
        activeSubtitlesList = SubtitleSerializer.deserializeSubtitles(video.subtitlesJson)
        currentPlaybackTime = 0f
        isPlaying = false
        playbackJob?.cancel()
        playbackJob = null
        currentSubtitleIndex = -1
        currentTab = AppTab.PLAYER
    }

    // Edit Subtitles
    fun updateSubtitleItem(id: Int, newTranslatedText: String) {
        activeSubtitlesList = activeSubtitlesList.map {
            if (it.id == id) it.copy(translatedText = newTranslatedText) else it
        }
        val currentVideo = activeDubbedVideo
        if (currentVideo != null) {
            val updatedJson = SubtitleSerializer.serializeSubtitles(activeSubtitlesList)
            val updatedVideo = currentVideo.copy(subtitlesJson = updatedJson)
            activeDubbedVideo = updatedVideo
            viewModelScope.launch {
                repository.updateVideo(updatedVideo)
            }
        }
    }

    // Delete a project from History
    fun deleteVideo(video: DubbedVideo) {
        viewModelScope.launch {
            repository.deleteVideo(video)
            if (activeDubbedVideo?.id == video.id) {
                activeDubbedVideo = null
                activeSubtitlesList = emptyList()
                isPlaying = false
            }
        }
    }
}
