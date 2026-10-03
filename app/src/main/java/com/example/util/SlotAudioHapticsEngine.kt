package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * High-Quality Audio & Haptics Engine for Slots Arena:
 * - Real-time synthesized audio waveforms (spin ticks, reel stop thud, Zeus thunder crash, win arpeggios, cheering)
 * - Hardware vibrator tactile feedback (spin click, reel stop, thunder rumble, big win pulse pattern)
 */
object SlotAudioHapticsEngine {

  private var vibrator: Vibrator? = null
  private val scope = CoroutineScope(Dispatchers.Default)

  fun init(context: Context) {
    if (vibrator == null) {
      vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
    }
  }

  // Haptic feedback methods
  fun vibrateSpinClick() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(25)
      }
    } catch (_: Exception) {}
  }

  fun vibrateReelStop() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(45, 180))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(45)
      }
    } catch (_: Exception) {}
  }

  fun vibrateThunderStrike() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pattern = longArrayOf(0, 60, 40, 120)
        val amplitudes = intArrayOf(0, 150, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(150)
      }
    } catch (_: Exception) {}
  }

  fun vibrateBigWin() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pattern = longArrayOf(0, 80, 50, 80, 50, 140, 60, 200)
        val amplitudes = intArrayOf(0, 200, 0, 220, 0, 240, 0, 255)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(longArrayOf(0, 80, 50, 80, 50, 140), -1)
      }
    } catch (_: Exception) {}
  }

  // Audio synthesis using AudioTrack
  private fun playSynthesizedTone(freqHz: Double, durationMs: Int, volume: Float = 0.5f, decay: Boolean = true) {
    scope.launch {
      try {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
          val time = i.toDouble() / sampleRate
          val envelope = if (decay) (1.0 - (i.toDouble() / numSamples)) else 1.0
          val sample = (sin(2.0 * Math.PI * freqHz * time) * 32767 * volume * envelope).toInt()
          buffer[i] = sample.coerceIn(-32768, 32767).toShort()
        }

        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(buffer.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        kotlinx.coroutines.delay(durationMs.toLong() + 50)
        track.release()
      } catch (_: Exception) {}
    }
  }

  fun playSpinTick() {
    playSynthesizedTone(freqHz = 880.0, durationMs = 35, volume = 0.35f, decay = true)
  }

  fun playReelStop() {
    playSynthesizedTone(freqHz = 220.0, durationMs = 70, volume = 0.5f, decay = true)
  }

  fun playZeusThunder() {
    scope.launch {
      vibrateThunderStrike()
      // Thunder crackle: burst of low frequency rumbling waves
      try {
        val sampleRate = 22050
        val durationMs = 450
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        var prev = 0.0

        for (i in 0 until numSamples) {
          val whiteNoise = (kotlin.random.Random.nextDouble() * 2.0 - 1.0)
          // Low-pass filter noise for heavy rumbling thunder sound
          prev = 0.85 * prev + 0.15 * whiteNoise
          val env = 1.0 - (i.toDouble() / numSamples)
          val rumble = sin(2.0 * Math.PI * 65.0 * (i.toDouble() / sampleRate))
          val sample = ((prev * 0.7 + rumble * 0.3) * 32767 * 0.75 * env).toInt()
          buffer[i] = sample.coerceIn(-32768, 32767).toShort()
        }

        val track = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_GAME)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(buffer.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        kotlinx.coroutines.delay(durationMs.toLong() + 50)
        track.release()
      } catch (_: Exception) {}
    }
  }

  fun playWinChime() {
    scope.launch {
      // Ascending C major arpeggio: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
      val notes = listOf(523.25, 659.25, 783.99, 1046.50)
      for (freq in notes) {
        playSynthesizedTone(freqHz = freq, durationMs = 80, volume = 0.6f)
        kotlinx.coroutines.delay(65L)
      }
    }
  }

  fun playMegaWinFanfare() {
    scope.launch {
      vibrateBigWin()
      // Joyous victory fanfare
      val fanfare = listOf(
        523.25 to 100, 659.25 to 100, 783.99 to 100,
        1046.50 to 180, 783.99 to 90, 1046.50 to 350
      )
      for ((freq, dur) in fanfare) {
        playSynthesizedTone(freqHz = freq, durationMs = dur, volume = 0.7f)
        kotlinx.coroutines.delay(dur.toLong())
      }
    }
  }

  fun playDancerCheer() {
    scope.launch {
      // Playful upbeat glissando whistle / cheer
      val notes = listOf(600.0, 750.0, 950.0, 1200.0)
      for (freq in notes) {
        playSynthesizedTone(freqHz = freq, durationMs = 50, volume = 0.5f)
        kotlinx.coroutines.delay(40L)
      }
    }
  }

  fun playDancerGiggle() {
    scope.launch {
      // Playful seductive giggle sound effect (staccato cheerful laughing notes)
      val giggleNotes = listOf(784.0, 880.0, 784.0, 987.0, 1046.0, 880.0)
      for (freq in giggleNotes) {
        playSynthesizedTone(freqHz = freq, durationMs = 38, volume = 0.45f, decay = true)
        kotlinx.coroutines.delay(45L)
      }
    }
  }

  fun playPlayfulKiss() {
    scope.launch {
      // Sweet kiss sound pop effect (quick harmonic swoosh)
      playSynthesizedTone(freqHz = 1100.0, durationMs = 25, volume = 0.55f, decay = true)
      kotlinx.coroutines.delay(20L)
      playSynthesizedTone(freqHz = 1480.0, durationMs = 45, volume = 0.6f, decay = true)
    }
  }

  fun playCoinShower() {
    scope.launch {
      // Cascade of golden coins clinking
      val coinFrequencies = listOf(1318.5, 1567.9, 1760.0, 1975.5, 2093.0, 1760.0, 2349.3)
      for (freq in coinFrequencies) {
        playSynthesizedTone(freqHz = freq, durationMs = 35, volume = 0.4f, decay = true)
        kotlinx.coroutines.delay(30L)
      }
    }
  }
}
