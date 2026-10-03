package com.example.service

import android.annotation.SuppressLint
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
import com.example.R
import java.util.concurrent.atomic.AtomicInteger

/**
 * Helper to manage push notifications for:
 * 1. 5 minutes before bet closure time
 * 2. Simulated matches reaching final results & ticket settlement
 * 3. Live goal alerts
 */
@SuppressLint("MissingPermission")
object PushNotificationHelper {

  const val CHANNEL_ID = "tahmin_arena_push_channel"
  const val CHANNEL_NAME = "Tahmin Arena Bildirimleri"
  const val CHANNEL_DESC = "Bahis kapanış uyarıları, maç sonuçları ve canlı gol bildirimleri"

  private val notificationIdCounter = AtomicInteger(1001)

  fun initChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_HIGH
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
        description = CHANNEL_DESC
        enableVibration(true)
        setShowBadge(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun hasNotificationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }

  /**
   * Alert user 5 minutes before bet closure time.
   */
  fun sendBetClosureAlert(context: Context, matchName: String, minutesRemaining: Int = 5) {
    try {
      initChannel(context)
      if (!hasNotificationPermission(context)) return

      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      val pendingIntent = PendingIntent.getActivity(
        context,
        notificationIdCounter.incrementAndGet(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
      )

      val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("⏱️ 5 Dakika Kuralı: Bahis Kapanıyor!")
        .setContentText("$matchName maçı için bahis alımı $minutesRemaining dakika sonra kapanıyor. Kuponunuzu şimdi onaylayın.")
        .setStyle(
          NotificationCompat.BigTextStyle().bigText(
            "$matchName karşılaşması başlamak üzere! 5 dakika kuralı gereğince oranlar kilitlenecektir. Kuponunuzu tamamlamak için son dakikalar."
          )
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

      NotificationManagerCompat.from(context).notify(notificationIdCounter.getAndIncrement(), notification)
    } catch (_: Exception) {
      // Graceful fallback
    }
  }

  /**
   * Alert user when simulated matches reach final results and ticket settlements.
   */
  fun sendMatchResultNotification(
    context: Context,
    ticketNumber: String,
    isWon: Boolean,
    wonPoints: Long,
    matchSummary: String
  ) {
    try {
      initChannel(context)
      if (!hasNotificationPermission(context)) return

      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      val pendingIntent = PendingIntent.getActivity(
        context,
        notificationIdCounter.incrementAndGet(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
      )

      val title = if (isWon) {
        "🏆 KUPONUNUZ KAZANDI! (+$wonPoints TP)"
      } else {
        "📋 Kupon Sonuçlandı: #$ticketNumber"
      }

      val content = if (isWon) {
        "Tebrikler! #$ticketNumber numaralı kuponunuz tuttu ve $wonPoints Tahmin Puanı kasanıza eklendi!"
      } else {
        "#$ticketNumber numaralı kuponunuzdaki maçlar tamamlandı. Sonuçları Kuponlarım sekmesinden inceleyebilirsiniz."
      }

      val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(if (isWon) android.R.drawable.star_big_on else android.R.drawable.ic_menu_agenda)
        .setContentTitle(title)
        .setContentText(content)
        .setStyle(
          NotificationCompat.BigTextStyle().bigText(
            "$content\n\nMaç Detayı: $matchSummary"
          )
        )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build()

      NotificationManagerCompat.from(context).notify(notificationIdCounter.getAndIncrement(), notification)
    } catch (_: Exception) {
      // Graceful fallback
    }
  }

  /**
   * Alert user for live goals in tracked/betted matches.
   */
  fun sendGoalNotification(
    context: Context,
    matchName: String,
    scoringTeam: String,
    minute: Int,
    newScore: String
  ) {
    try {
      initChannel(context)
      if (!hasNotificationPermission(context)) return

      val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("⚽ GOOOL! $scoringTeam ($minute')")
        .setContentText("$matchName • Yeni Skor: $newScore")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

      NotificationManagerCompat.from(context).notify(notificationIdCounter.getAndIncrement(), notification)
    } catch (_: Exception) {
      // Graceful fallback
    }
  }
}
