package com.wgm.quiz.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.sin

/**
 * Pure Kotlin AudioTrack synthesizer for generating real-time sine-wave arcade tones.
 * Used for supplementary UI interaction sounds without needing external audio assets.
 */
object WgmSynthesizer {

    private const val SAMPLE_RATE = 44100

    /**
     * Play a short sine-wave beep.
     * @param frequencyHz Frequency in Hz (e.g., 880.0 for A5)
     * @param durationMs Duration in milliseconds
     * @param volume Volume from 0.0 to 1.0
     */
    suspend fun playTone(
        frequencyHz: Double = 880.0,
        durationMs: Int = 100,
        volume: Float = 0.3f
    ) = withContext(Dispatchers.IO) {
        try {
            val numSamples = (SAMPLE_RATE * durationMs / 1000.0).toInt()
            val samples = ShortArray(numSamples)
            
            for (i in 0 until numSamples) {
                val angle = 2.0 * PI * i / (SAMPLE_RATE / frequencyHz)
                samples[i] = (sin(angle) * Short.MAX_VALUE * volume).toInt().toShort()
            }

            // Apply fade-in/fade-out to avoid clicks
            val fadeLength = minOf(numSamples / 10, 200)
            for (i in 0 until fadeLength) {
                val fade = i.toFloat() / fadeLength
                samples[i] = (samples[i] * fade).toInt().toShort()
                samples[numSamples - 1 - i] = (samples[numSamples - 1 - i] * fade).toInt().toShort()
            }

            val bufferSize = samples.size * 2 // 16-bit = 2 bytes per sample
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STATIC
            )
            
            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            
            // Wait for playback to finish, then release
            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Play a rising two-tone beep (used for lifeline activation).
     */
    suspend fun playLifelineActivation() {
        playTone(frequencyHz = 660.0, durationMs = 80, volume = 0.25f)
        playTone(frequencyHz = 880.0, durationMs = 120, volume = 0.3f)
    }

    /**
     * Play a short click tone (used for button presses).
     */
    suspend fun playClick() {
        playTone(frequencyHz = 1200.0, durationMs = 30, volume = 0.15f)
    }
}
