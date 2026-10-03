package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Provides haptic feedback when a goal is scored in the user's coupon.
 */
object VibrationHelper {

  fun vibrateGoal(context: Context) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        val vibrator = vibratorManager?.defaultVibrator
        vibrator?.vibrate(
          VibrationEffect.createWaveform(longArrayOf(0, 180, 80, 220), -1)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(
            VibrationEffect.createWaveform(longArrayOf(0, 180, 80, 220), -1)
          )
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(300)
        }
      }
    } catch (_: Exception) {
      // Graceful fallback if vibration permission or hardware is unavailable
    }
  }
}
