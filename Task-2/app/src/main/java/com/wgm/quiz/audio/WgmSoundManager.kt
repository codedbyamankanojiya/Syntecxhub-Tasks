package com.wgm.quiz.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.*

/**
 * Centralized audio manager for all game sound effects.
 * Uses MediaPlayer for background loops (Timer, Question) and SoundPool for short SFX.
 */
class WgmSoundManager(private val context: Context) {

    enum class WgmSound {
        LOCK,
        TIMER_LOOP,
        QUESTION_BG,
        CORRECT,
        WRONG,
        TIME_UP
    }

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(audioAttributes)
        .build()

    // Coroutine scope for sound management
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // SoundPool IDs for short SFX
    private var lockSoundId: Int = 0
    private var correctSoundId: Int = 0
    private var wrongSoundId: Int = 0
    private var timeUpSoundId: Int = 0

    // Tracking stream IDs to allow stopping specific SoundPool sounds
    private val activeStreams = mutableMapOf<WgmSound, Int>()

    // MediaPlayer for looping sounds
    private var timerMediaPlayer: MediaPlayer? = null
    private var questionMediaPlayer: MediaPlayer? = null
    
    private var isTimerPlaying = false
    private var isQuestionPlaying = false
    
    private var isTimerPrepared = false
    private var isQuestionPrepared = false
    
    private var isSfxLoaded = false
    private var loadedSamplesCount = 0
    private val totalSfxToLoad = 4

    init {
        Log.d("WgmSoundManager", "init: Initializing SoundManager")
        serviceScope.launch {
            withContext(Dispatchers.IO) {
                preloadSounds()
                prepareTimerPlayer()
                prepareQuestionPlayer()
            }
        }
    }

    private fun prepareTimerPlayer() {
        try {
            timerMediaPlayer?.release()
            isTimerPrepared = false
            timerMediaPlayer = MediaPlayer().apply {
                val fd = context.assets.openFd("Timer.mp3")
                setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                fd.close()
                setAudioAttributes(audioAttributes)
                isLooping = true
                setOnPreparedListener {
                    isTimerPrepared = true
                    if (isTimerPlaying) it.start()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Failed to prepare timer player", e)
        }
    }

    private fun prepareQuestionPlayer() {
        try {
            questionMediaPlayer?.release()
            isQuestionPrepared = false
            questionMediaPlayer = MediaPlayer().apply {
                val fd = context.assets.openFd("Question.mp3")
                setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                fd.close()
                setAudioAttributes(audioAttributes)
                isLooping = true
                setOnPreparedListener {
                    isQuestionPrepared = true
                    if (isQuestionPlaying) it.start()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Failed to prepare question player", e)
        }
    }

    private fun preloadSounds() {
        try {
            val assetManager = context.assets
            
            assetManager.openFd("Lock.mp3").use { fd -> lockSoundId = soundPool.load(fd, 1) }
            assetManager.openFd("Right Answer.mp3").use { fd -> correctSoundId = soundPool.load(fd, 1) }
            assetManager.openFd("Wrong Answer.mp3").use { fd -> wrongSoundId = soundPool.load(fd, 1) }
            assetManager.openFd("Time Up.mp3").use { fd -> timeUpSoundId = soundPool.load(fd, 1) }

            soundPool.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) {
                    loadedSamplesCount++
                    if (loadedSamplesCount >= totalSfxToLoad) isSfxLoaded = true
                }
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Failed to preload sounds", e)
        }
    }

    fun play(sound: WgmSound) {
        Log.d("WgmSoundManager", "play: Triggering sound $sound")
        when (sound) {
            WgmSound.LOCK -> {
                stopSfx(WgmSound.LOCK)
                activeStreams[sound] = soundPool.play(lockSoundId, 1f, 1f, 1, 0, 1f)
            }
            WgmSound.CORRECT -> {
                // Stop LOCK or other result SFX before playing CORRECT
                stopAllSfx()
                activeStreams[sound] = soundPool.play(correctSoundId, 1f, 1f, 1, 0, 1f)
            }
            WgmSound.WRONG -> {
                // Stop LOCK or other result SFX before playing WRONG
                stopAllSfx()
                activeStreams[sound] = soundPool.play(wrongSoundId, 1f, 1f, 1, 0, 1f)
            }
            WgmSound.TIME_UP -> {
                stopAll()
                activeStreams[sound] = soundPool.play(timeUpSoundId, 1f, 1f, 1, 0, 1f)
            }
            WgmSound.TIMER_LOOP -> startTimerLoop()
            WgmSound.QUESTION_BG -> playQuestionBg()
        }
    }

    private fun stopSfx(sound: WgmSound) {
        activeStreams[sound]?.let { streamId ->
            soundPool.stop(streamId)
            activeStreams.remove(sound)
        }
    }

    private fun stopAllSfx() {
        Log.d("WgmSoundManager", "stopAllSfx: Stopping all active SFX streams")
        activeStreams.forEach { (_, streamId) ->
            soundPool.stop(streamId)
        }
        activeStreams.clear()
        // Force stop all SoundPool streams to ensure robustness
        soundPool.autoPause()
    }

    /**
     * Halts all current audio (both looping MediaPlayers and short SFX).
     */
    fun stopAll() {
        Log.d("WgmSoundManager", "stopAll: Halting all audio components")
        stopTimerLoop()
        stopQuestionBg()
        stopAllSfx()
    }

    private fun startTimerLoop() {
        Log.d("WgmSoundManager", "startTimerLoop: Starting timer music")
        isTimerPlaying = true
        if (isTimerPrepared) {
            timerMediaPlayer?.seekTo(0)
            timerMediaPlayer?.start()
        }
    }

    fun stopTimerLoop() {
        isTimerPlaying = false
        timerMediaPlayer?.takeIf { it.isPlaying }?.let {
            it.pause()
            it.seekTo(0)
        }
    }

    fun playQuestionBg() {
        Log.d("WgmSoundManager", "playQuestionBg: Starting question background music")
        isQuestionPlaying = true
        if (isQuestionPrepared) {
            questionMediaPlayer?.seekTo(0)
            questionMediaPlayer?.start()
        }
    }

    fun stopQuestionBg() {
        isQuestionPlaying = false
        questionMediaPlayer?.takeIf { it.isPlaying }?.let {
            it.pause()
            it.seekTo(0)
        }
    }

    fun onPause() {
        timerMediaPlayer?.takeIf { it.isPlaying }?.pause()
        questionMediaPlayer?.takeIf { it.isPlaying }?.pause()
        soundPool.autoPause()
    }

    fun onResume() {
        if (isTimerPlaying && isTimerPrepared) timerMediaPlayer?.start()
        if (isQuestionPlaying && isQuestionPrepared) questionMediaPlayer?.start()
        soundPool.autoResume()
    }

    fun release() {
        serviceScope.cancel()
        timerMediaPlayer?.release()
        questionMediaPlayer?.release()
        soundPool.release()
    }
}
