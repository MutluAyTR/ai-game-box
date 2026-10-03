package com.example.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

/**
 * System Local Notification Manager for TahminArena.
 * Alerts users on:
 * - Match kickoffs
 * - Live goals in followed / major derbies
 * - 85th-minute betting closures
 * - High-confidence AI smart predictions
 * - Ticket settlement results (Won/Lost TP payouts)
 */
object LocalNotificationManager {

  private const val CHANNEL_MATCH_ALERTS = "channel_tahmin_arena_matches"
  private const val CHANNEL_AI_PREDICTIONS = "channel_tahmin_arena_ai"
  private const val CHANNEL_BETS = "channel_tahmin_arena_bets"

  fun createNotificationChannels(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        ?: return

      val matchChannel = NotificationChannel(
        CHANNEL_MATCH_ALERTS,
        "Canlı Maç & Gol Bildirimleri",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Canlı goller, derbi başlama uyarıları ve 85' bahis kilidi"
        enableVibration(true)
      }

      val aiChannel = NotificationChannel(
        CHANNEL_AI_PREDICTIONS,
        "AI Akıllı Tahmin Bildirimleri",
        NotificationManager.IMPORTANCE_DEFAULT
      ).apply {
        description = "%80+ Güven oranlı yapay zeka analiz ve kupon fırsatları"
      }

      val betChannel = NotificationChannel(
        CHANNEL_BETS,
        "Kupon & Bakiye Bildirimleri",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Kupon kazançları ve sanal TP bakiye güncellemeleri"
        enableVibration(true)
      }

      notificationManager.createNotificationChannels(listOf(matchChannel, aiChannel, betChannel))
    }
  }

  fun notifyMatchStarting(context: Context, matchTitle: String, kickoffTime: String) {
    if (!hasPermission(context)) return

    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
      context, 1001, intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_MATCH_ALERTS)
      .setSmallIcon(android.R.drawable.ic_popup_reminder)
      .setContentTitle("⚽ Derbi Başlamak Üzere!")
      .setContentText("$matchTitle maçı $kickoffTime itibarıyla başlıyor. Oranlar kapanmadan kuponunu yap!")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    try {
      NotificationManagerCompat.from(context).notify(1001, notification)
    } catch (_: SecurityException) {}
  }

  fun notifyGoal(context: Context, matchTitle: String, minute: Int, scorer: String, newScore: String) {
    if (!hasPermission(context)) return

    val notification = NotificationCompat.Builder(context, CHANNEL_MATCH_ALERTS)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("⚽ GOOOL! ($newScore) • $minute'")
      .setContentText("$matchTitle: $scorer ağları havalandırdı! Canlı oranlar güncelleniyor.")
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .build()

    try {
      NotificationManagerCompat.from(context).notify(1002, notification)
    } catch (_: SecurityException) {}
  }

  fun notifyBettingLocked(context: Context, matchTitle: String) {
    if (!hasPermission(context)) return

    val notification = NotificationCompat.Builder(context, CHANNEL_MATCH_ALERTS)
      .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
      .setContentTitle("🔒 85' Dakika: Canlı İddia Kapandı")
      .setContentText("$matchTitle maçında 85. dakikaya girildi. Canlı bahis alımları kilitlendi.")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()

    try {
      NotificationManagerCompat.from(context).notify(1003, notification)
    } catch (_: SecurityException) {}
  }

  fun notifyHighAiPrediction(context: Context, matchTitle: String, tip: String, confidence: Int) {
    if (!hasPermission(context)) return

    val notification = NotificationCompat.Builder(context, CHANNEL_AI_PREDICTIONS)
      .setSmallIcon(android.R.drawable.ic_menu_compass)
      .setContentTitle("🤖 Yüksek Güvenli AI Tahmini (%${confidence})")
      .setContentText("$matchTitle için yapay zeka banko tercihi belirledi: $tip")
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .setAutoCancel(true)
      .build()

    try {
      NotificationManagerCompat.from(context).notify(1004, notification)
    } catch (_: SecurityException) {}
  }

  fun notifyBetSettled(context: Context, ticketNumber: String, isWon: Boolean, returnPoints: Long) {
    if (!hasPermission(context)) return

    val title = if (isWon) "🎉 TEBRİKLER! Kuponun Kazandı!" else "Kupon Sonuçlandı"
    val text = if (isWon) {
      "#$ticketNumber kuponun kazandı! +$returnPoints TP sanal bakiyene eklendi."
    } else {
      "#$ticketNumber kuponun sonuçlandı."
    }

    val notification = NotificationCompat.Builder(context, CHANNEL_BETS)
      .setSmallIcon(if (isWon) android.R.drawable.star_big_on else android.R.drawable.ic_menu_agenda)
      .setContentTitle(title)
      .setContentText(text)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .build()

    try {
      NotificationManagerCompat.from(context).notify(1005, notification)
    } catch (_: SecurityException) {}
  }

  private fun hasPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }
}
