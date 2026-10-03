package com.example

import android.app.Application
import android.util.Log
import com.example.service.LocalNotificationManager
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class TahminArenaApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    com.example.service.CalendarManagementService.initContext(this)
    initFirebase(this)
    LocalNotificationManager.createNotificationChannels(this)
  }

  companion object {
    fun initFirebase(app: Application) {
      try {
        if (FirebaseApp.getApps(app).isEmpty()) {
          try {
            FirebaseApp.initializeApp(app)
          } catch (_: Throwable) {
            val options = FirebaseOptions.Builder()
              .setApplicationId("1:100000000000:android:tahminarenasim")
              .setApiKey("AIzaSyTahminArenaLocalFallbackKey0000")
              .setProjectId("tahminarena-sim")
              .build()
            FirebaseApp.initializeApp(app, options)
            Log.d("TahminArenaApp", "FirebaseApp initialized with local fallback options.")
          }
        }
      } catch (t: Throwable) {
        Log.w("TahminArenaApp", "FirebaseApp init warning: ${t.message}")
      }
    }
  }
}
