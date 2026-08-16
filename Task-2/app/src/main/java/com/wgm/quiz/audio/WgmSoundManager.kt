package com.wgm.quiz.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import kotlinx.coroutines.*

/**
 * Centralized audio manager for all game sound effects.
 * Uses MediaPlayer for background loops (Timer) and SoundPool for short SFX.
 */
class WgmSoundManager(private val context: Context) {

    enum class WgmSound {
        LOCK,
        TIMER_LOOP,
        QUESTION_INTRO,
        CORRECT,
        WRONG,
        TIME_UP,
        CLICK
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
    private var questionIntroSoundId: Int = 0
    private var clickSoundId: Int = 0

    // MediaPlayer for looping timer sound
    private var timerMediaPlayer: MediaPlayer? = null
    private var isTimerPlaying = false
    private var isTimerPrepared = false
    private var isSfxLoaded = false
    private var loadedSamplesCount = 0
    private val totalSfxToLoad = 6

    init {
        Log.d("WgmSoundManager", "init: Initializing SoundManager")
        serviceScope.launch {
            withContext(Dispatchers.IO) {
                preloadSounds()
                prepareTimerPlayer()
            }
        }
    }

    private fun prepareTimerPlayer() {
        try {
            Log.d("WgmSoundManager", "prepareTimerPlayer: Starting MediaPlayer preparation")
            timerMediaPlayer?.release()
            isTimerPrepared = false
            timerMediaPlayer = MediaPlayer().apply {
                val fd = context.assets.openFd("Timer.mp3")
                setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                fd.close()
                setAudioAttributes(audioAttributes)
                isLooping = true
                setOnPreparedListener {
                    Log.d("WgmSoundManager", "prepareTimerPlayer: MediaPlayer prepared successfully")
                    isTimerPrepared = true
                    if (isTimerPlaying) {
                        it.start()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("WgmSoundManager", "MediaPlayer error: $what, $extra")
                    isTimerPrepared = false
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Failed to prepare timer player", e)
        }
    }

    private fun preloadSounds() {
        try {
            Log.d("WgmSoundManager", "preloadSounds: Starting SFX preloading")
            val assetManager = context.assets
            
            // Load short SFX into SoundPool
            assetManager.openFd("Lock.mp3").use { fd ->
                lockSoundId = soundPool.load(fd, 1)
            }
            assetManager.openFd("Right Answer.mp3").use { fd ->
                correctSoundId = soundPool.load(fd, 1)
            }
            assetManager.openFd("Wrong Answer.mp3").use { fd ->
                wrongSoundId = soundPool.load(fd, 1)
            }
            assetManager.openFd("Time Up.mp3").use { fd ->
                timeUpSoundId = soundPool.load(fd, 1)
            }
            assetManager.openFd("Question.mp3").use { fd ->
                questionIntroSoundId = soundPool.load(fd, 1)
            }
            assetManager.openFd("Finger .mp3").use { fd ->
                clickSoundId = soundPool.load(fd, 1)
            }

            soundPool.setOnLoadCompleteListener { _, sampleId, status ->
                Log.d("WgmSoundManager", "preloadSounds: Sample $sampleId loaded with status: $status")
                if (status == 0) {
                    loadedSamplesCount++
                    if (loadedSamplesCount >= totalSfxToLoad) {
                        isSfxLoaded = true
                        Log.i("WgmSoundManager", "preloadSounds: ALL SFX LOADED SUCCESSFULLY")
                    }
                } else {
                    Log.e("WgmSoundManager", "preloadSounds: Failed to load sample $sampleId, status: $status")
                }
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Failed to preload sounds", e)
        }
    }

    /**
     * Play a one-shot sound effect.
     */
    fun play(sound: WgmSound) {
        Log.d("WgmSoundManager", "play() called for sound: $sound (isSfxLoaded: $isSfxLoaded, isTimerPrepared: $isTimerPrepared)")
        
        when (sound) {
            WgmSound.LOCK -> {
                val res = soundPool.play(lockSoundId, 1f, 1f, 1, 0, 1f)
                Log.d("WgmSoundManager", "Playing LOCK, res: $res")
            }
            WgmSound.CORRECT -> {
                val res = soundPool.play(correctSoundId, 1f, 1f, 1, 0, 1f)
                Log.d("WgmSoundManager", "Playing CORRECT, res: $res")
            }
            WgmSound.WRONG -> {
                val res = soundPool.play(wrongSoundId, 1f, 1f, 1, 0, 1f)
                Log.d("WgmSoundManager", "Playing WRONG, res: $res")
            }
            WgmSound.TIME_UP -> {
                val res = soundPool.play(timeUpSoundId, 1f, 1f, 1, 0, 1f)
                Log.d("WgmSoundManager", "Playing TIME_UP, res: $res")
            }
            WgmSound.QUESTION_INTRO -> {
                val res = soundPool.play(questionIntroSoundId, 1f, 1f, 1, 0, 1f)
                Log.d("WgmSoundManager", "Playing QUESTION_INTRO, res: $res")
            }
            WgmSound.CLICK -> {
                val res = soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
                Log.d("WgmSoundManager", "Playing CLICK, res: $res")
            }
            WgmSound.TIMER_LOOP -> startTimerLoop()
        }
    }

    /**
     * Start the timer background loop using the pre-prepared MediaPlayer.
     */
    private fun startTimerLoop() {
        isTimerPlaying = true
        Log.d("WgmSoundManager", "startTimerLoop: Requested (isTimerPrepared: $isTimerPrepared)")
        try {
            if (timerMediaPlayer == null) {
                Log.w("WgmSoundManager", "startTimerLoop: MediaPlayer is null, preparing...")
                prepareTimerPlayer()
            } else if (isTimerPrepared) {
                timerMediaPlayer?.let {
                    if (it.isPlaying) it.pause()
                    it.seekTo(0)
                    it.setVolume(1.0f, 1.0f)
                    it.start()
                    Log.d("WgmSoundManager", "startTimerLoop: MediaPlayer STARTED")
                }
            } else {
                Log.w("WgmSoundManager", "startTimerLoop: MediaPlayer not prepared yet, will start automatically when ready")
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Error starting timer loop", e)
        }
    }

    /**
     * Stop the timer loop without releasing, for quick reuse.
     */
    fun stopTimerLoop() {
        isTimerPlaying = false
        try {
            timerMediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    it.seekTo(0)
                }
            }
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Error stopping timer loop", e)
        }
    }

    /**
     * Pause all audio (call from Activity onPause).
     */
    fun onPause() {
        try {
            timerMediaPlayer?.let {
                if (it.isPlaying) it.pause()
            }
            soundPool.autoPause()
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Error in onPause", e)
        }
    }

    /**
     * Resume audio (call from Activity onResume).
     */
    fun onResume() {
        try {
            if (isTimerPlaying && isTimerPrepared) {
                timerMediaPlayer?.start()
            }
            soundPool.autoResume()
        } catch (e: Exception) {
            Log.e("WgmSoundManager", "Error in onResume", e)
        }
    }

    /**
     * Release all resources (call from Application onTerminate or Activity onDestroy).
     */
    fun release() {
        serviceScope.cancel()
        stopTimerLoop()
        timerMediaPlayer?.release()
        timerMediaPlayer = null
        soundPool.release()
    }
}
