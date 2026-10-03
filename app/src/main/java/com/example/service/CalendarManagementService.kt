package com.example.service

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Fixture Day Cluster representation.
 */
data class FixtureCluster(
  val id: String,
  val title: String,
  val subtitle: String,
  val startDateIso: String,
  val endDateIso: String,
  val dates: List<String>
)

/**
 * Status of betting availability for a match based on the 5-minute pre-match closure window.
 */
sealed class BettingClosureStatus {
  data class Open(val remainingMinutes: Long, val formattedRemaining: String) : BettingClosureStatus()
  data class Locked5MinWindow(val startsInMinutes: Long, val formattedTime: String) : BettingClosureStatus()
  object LiveInPlay : BettingClosureStatus()
  object Finished : BettingClosureStatus()
}

/**
 * Calendar Management Service
 * 
 * 1. Manages continuous time flow and state persistence across app open/close cycles.
 * 2. Days progress naturally in real-time (26 Eylül -> 27 Eylül -> 28 Eylül -> 29 Eylül ...).
 * 3. Never resets back to 26 September on restart.
 * 4. Aligns the app's fixture schedule with dynamic dates and strictly prevents betting
 *    on matches after the pre-defined 5-minute closure window.
 */
object CalendarManagementService {

  // 5-minute closure window in milliseconds
  const val CLOSURE_WINDOW_MS: Long = 5 * 60 * 1000L

  val turkeyTimeZone: TimeZone = TimeZone.getTimeZone("GMT+3")

  private const val PREFS_NAME = "tahmin_arena_calendar_prefs"
  private const val KEY_SIMULATED_TIME = "simulated_time_ms"
  private const val KEY_WALL_CLOCK_TIME = "wall_clock_time_ms"
  private const val KEY_MANUAL_DAY_OFFSET = "manual_day_offset"

  private var sharedPreferences: SharedPreferences? = null

  // Fallback initial base time in Turkey Time (Today 15:30 TSİ)
  private val initialSeedBaseTime: Long = run {
    val cal = Calendar.getInstance(turkeyTimeZone)
    cal.set(Calendar.HOUR_OF_DAY, 15)
    cal.set(Calendar.MINUTE, 30)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal.timeInMillis
  }

  @Volatile
  private var activeSimulatedBaseTime: Long = initialSeedBaseTime

  @Volatile
  private var activeRealAnchorTime: Long = System.currentTimeMillis()

  private var lastPersistTime: Long = 0L

  /**
   * Initializes persistent storage for continuous time progression across app restarts.
   */
  fun initContext(context: Context) {
    val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    sharedPreferences = prefs

    val savedSimulated = prefs.getLong(KEY_SIMULATED_TIME, 0L)
    val savedWall = prefs.getLong(KEY_WALL_CLOCK_TIME, 0L)

    val currentWall = System.currentTimeMillis()

    if (savedSimulated > 0L && savedWall > 0L) {
      // Clamp elapsed time jump to prevent sudden multiple-day time warps on mobile installs
      val realElapsed = (currentWall - savedWall).coerceIn(0L, 60 * 60 * 1000L)
      activeSimulatedBaseTime = savedSimulated + realElapsed
      activeRealAnchorTime = currentWall
    } else {
      activeSimulatedBaseTime = initialSeedBaseTime
      activeRealAnchorTime = currentWall
      persistCurrentTime()
    }
  }

  /**
   * Called on app foregrounding to re-synchronize elapsed real time if the app remained suspended.
   */
  fun onAppResume() {
    val prefs = sharedPreferences ?: return
    val savedSimulated = prefs.getLong(KEY_SIMULATED_TIME, 0L)
    val savedWall = prefs.getLong(KEY_WALL_CLOCK_TIME, 0L)
    val currentWall = System.currentTimeMillis()

    if (savedSimulated > 0L && savedWall > 0L) {
      val realElapsed = (currentWall - savedWall).coerceIn(0L, 60 * 60 * 1000L)
      if (realElapsed > 1000L) {
        activeSimulatedBaseTime = savedSimulated + realElapsed
        activeRealAnchorTime = currentWall
      }
    }
    persistCurrentTime()
  }

  /**
   * Persists simulated clock time to permanent storage synchronously to prevent data loss on kill.
   */
  fun persistCurrentTime() {
    val prefs = sharedPreferences ?: return
    val currentWall = System.currentTimeMillis()
    val elapsed = (currentWall - activeRealAnchorTime).coerceAtLeast(0L)
    val currentSimulated = activeSimulatedBaseTime + elapsed

    prefs.edit()
      .putLong(KEY_SIMULATED_TIME, currentSimulated)
      .putLong(KEY_WALL_CLOCK_TIME, currentWall)
      .commit()
  }

  /**
   * Returns current simulated application time with real-time natural progression.
   * Never resets when closing or opening the application.
   */
  fun getCurrentTimeMillis(): Long {
    val elapsed = System.currentTimeMillis() - activeRealAnchorTime
    val now = activeSimulatedBaseTime + elapsed

    // Periodically auto-save every 5 seconds
    val wall = System.currentTimeMillis()
    if (wall - lastPersistTime > 5000L) {
      lastPersistTime = wall
      persistCurrentTime()
    }

    return now
  }

  /**
   * Manually or automatically advances the simulation by given days (for AI day progression).
   */
  fun advanceDays(days: Int) {
    activeSimulatedBaseTime += (days * 24L * 60L * 60L * 1000L)
    persistCurrentTime()
  }

  /**
   * Returns current active date ISO string (e.g. "2026-09-26", "2026-09-27", "2026-09-28").
   */
  fun getCurrentDateIso(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = turkeyTimeZone }
    return sdf.format(Date(getCurrentTimeMillis()))
  }

  /**
   * Returns day progression fraction (0.0 to 1.0) for the current 24-hour cycle.
   */
  fun getDayProgressFraction(): Float {
    val cal = Calendar.getInstance(turkeyTimeZone).apply {
      timeInMillis = getCurrentTimeMillis()
    }
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val minute = cal.get(Calendar.MINUTE)
    val second = cal.get(Calendar.SECOND)
    val totalSeconds = hour * 3600 + minute * 60 + second
    return (totalSeconds / 86400f).coerceIn(0f, 1f)
  }

  /**
   * Formats the current time in TSİ.
   */
  fun getCurrentFormattedClock(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.US).apply { timeZone = turkeyTimeZone }
    return sdf.format(Date(getCurrentTimeMillis()))
  }

  /**
   * Formats the current full date and day in Turkish.
   */
  fun getCurrentFormattedDateWithDay(): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy, EEEE", Locale.forLanguageTag("tr-TR")).apply {
      timeZone = turkeyTimeZone
    }
    return sdf.format(Date(getCurrentTimeMillis()))
  }

  /**
   * Dynamic Real-World Match-Day Clusters that adapt to the active date.
   */
  fun getDynamicFixtureClusters(currentTimeMs: Long = getCurrentTimeMillis()): List<FixtureCluster> {
    val cal = Calendar.getInstance(turkeyTimeZone).apply { timeInMillis = currentTimeMs }
    val isoSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = turkeyTimeZone }
    val displaySdf = SimpleDateFormat("dd MMMM EEEE", Locale.forLanguageTag("tr-TR")).apply { timeZone = turkeyTimeZone }

    // Today
    val todayIso = isoSdf.format(cal.time)
    val todayDisplay = displaySdf.format(cal.time)

    // Yesterday
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterdayIso = isoSdf.format(cal.time)
    val yesterdayDisplay = displaySdf.format(cal.time)

    // Tomorrow
    cal.timeInMillis = currentTimeMs
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowIso = isoSdf.format(cal.time)
    val tomorrowDisplay = displaySdf.format(cal.time)

    // Week 2 (+7 days)
    cal.timeInMillis = currentTimeMs
    cal.add(Calendar.DAY_OF_YEAR, 7)
    val week2StartIso = isoSdf.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val week2EndIso = isoSdf.format(cal.time)

    // Week 3 (+14 days)
    cal.timeInMillis = currentTimeMs
    cal.add(Calendar.DAY_OF_YEAR, 14)
    val week3StartIso = isoSdf.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val week3EndIso = isoSdf.format(cal.time)

    return listOf(
      FixtureCluster(
        id = "cluster_yesterday",
        title = "Dün (Bitenler)",
        subtitle = yesterdayDisplay,
        startDateIso = yesterdayIso,
        endDateIso = yesterdayIso,
        dates = listOf(yesterdayIso)
      ),
      FixtureCluster(
        id = "cluster_today",
        title = "Bugün (Canlı Akış)",
        subtitle = todayDisplay,
        startDateIso = todayIso,
        endDateIso = todayIso,
        dates = listOf(todayIso)
      ),
      FixtureCluster(
        id = "cluster_tomorrow",
        title = "Yarın Bülteni",
        subtitle = tomorrowDisplay,
        startDateIso = tomorrowIso,
        endDateIso = tomorrowIso,
        dates = listOf(tomorrowIso)
      ),
      FixtureCluster(
        id = "cluster_week2",
        title = "2. Hafta (+1 Hafta)",
        subtitle = "Gelecek Hafta Bülteni",
        startDateIso = week2StartIso,
        endDateIso = week2EndIso,
        dates = listOf(week2StartIso, week2EndIso)
      ),
      FixtureCluster(
        id = "cluster_week3",
        title = "3. Hafta (+2 Hafta)",
        subtitle = "İlerleyen Fikstür",
        startDateIso = week3StartIso,
        endDateIso = week3EndIso,
        dates = listOf(week3StartIso, week3EndIso)
      )
    )
  }

  val fixtureClusters: List<FixtureCluster>
    get() = getDynamicFixtureClusters()

  fun getYesterdayDateIso(): String {
    val cal = Calendar.getInstance(turkeyTimeZone).apply {
      timeInMillis = getCurrentTimeMillis()
      add(Calendar.DAY_OF_YEAR, -1)
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = turkeyTimeZone }
    return sdf.format(cal.time)
  }

  fun getTomorrowDateIso(): String {
    val cal = Calendar.getInstance(turkeyTimeZone).apply {
      timeInMillis = getCurrentTimeMillis()
      add(Calendar.DAY_OF_YEAR, 1)
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = turkeyTimeZone }
    return sdf.format(cal.time)
  }

  fun getWeek2DateIso(): String {
    val cal = Calendar.getInstance(turkeyTimeZone).apply {
      timeInMillis = getCurrentTimeMillis()
      add(Calendar.DAY_OF_YEAR, 7)
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = turkeyTimeZone }
    return sdf.format(cal.time)
  }

  fun getWeek3DateIso(): String {
    val cal = Calendar.getInstance(turkeyTimeZone).apply {
      timeInMillis = getCurrentTimeMillis()
      add(Calendar.DAY_OF_YEAR, 14)
    }
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = turkeyTimeZone }
    return sdf.format(cal.time)
  }

  /**
   * Calculates the exact kickoff epoch timestamp for a given match.
   */
  fun getKickoffTimestamp(match: Match): Long {
    return try {
      val todayIso = getCurrentDateIso()
      val datePart = if (match.matchDateIso.isNotBlank()) {
        if ((match.status == MatchStatus.SCHEDULED || match.status == MatchStatus.UPCOMING) && match.matchDateIso < todayIso) {
          todayIso
        } else {
          match.matchDateIso
        }
      } else {
        todayIso
      }
      val timePart = match.startTime.ifBlank { "20:00" }
      val pattern = "yyyy-MM-dd HH:mm"
      val sdf = SimpleDateFormat(pattern, Locale.ROOT).apply { timeZone = turkeyTimeZone }
      val date = sdf.parse("$datePart $timePart")
      date?.time ?: (getCurrentTimeMillis() + 60 * 60 * 1000L)
    } catch (_: Exception) {
      getCurrentTimeMillis() + 60 * 60 * 1000L
    }
  }

  /**
   * Determines if betting on a match is closed based on the 5-minute pre-match closure window.
   */
  fun isBettingClosed(match: Match, currentTimeMs: Long = getCurrentTimeMillis()): Boolean {
    if (match.status == MatchStatus.FINISHED) return true

    // If live, lock betting when minute reaches 85
    if (match.status == MatchStatus.LIVE) {
      return match.minute >= 85
    }

    val kickoff = getKickoffTimestamp(match)
    val closureTime = kickoff - CLOSURE_WINDOW_MS
    return currentTimeMs >= closureTime
  }

  /**
   * Returns a detailed status object indicating whether betting is open or locked.
   */
  fun getClosureStatus(match: Match, currentTimeMs: Long = getCurrentTimeMillis()): BettingClosureStatus {
    if (match.status == MatchStatus.FINISHED) {
      return BettingClosureStatus.Finished
    }
    if (match.status == MatchStatus.LIVE) {
      if (match.minute >= 85) {
        return BettingClosureStatus.Locked5MinWindow(0, "85' Canlı Kilitli")
      }
      return BettingClosureStatus.LiveInPlay
    }

    val kickoff = getKickoffTimestamp(match)
    val closureTime = kickoff - CLOSURE_WINDOW_MS
    val remainingMs = closureTime - currentTimeMs

    return if (remainingMs <= 0) {
      val startsInMs = (kickoff - currentTimeMs).coerceAtLeast(0)
      val startsInMins = startsInMs / (60 * 1000L)
      BettingClosureStatus.Locked5MinWindow(
        startsInMinutes = startsInMins,
        formattedTime = if (startsInMins > 0) "Başlamaya $startsInMins dk (KİLİTLİ)" else "Başlamak üzere (KİLİTLİ)"
      )
    } else {
      val remainingMins = remainingMs / (60 * 1000L)
      val hours = remainingMins / 60
      val mins = remainingMins % 60
      val formatted = if (hours > 0) {
        "${hours}s ${mins}dk"
      } else {
        "${mins}dk"
      }
      BettingClosureStatus.Open(
        remainingMinutes = remainingMins,
        formattedRemaining = formatted
      )
    }
  }

  /**
   * Validates a list of slip selections before placing a bet.
   */
  fun validateSelectionsForBetting(
    selections: List<SlipSelection>,
    allMatches: List<Match>,
    currentTimeMs: Long = getCurrentTimeMillis()
  ): Result<Unit> {
    if (selections.isEmpty()) {
      return Result.failure(Exception("Kuponda en az 1 karşılaşma bulunmalıdır."))
    }

    val matchMap = allMatches.associateBy { it.id }

    for (sel in selections) {
      val match = matchMap[sel.matchId] ?: continue

      if (match.status == MatchStatus.FINISHED) {
        return Result.failure(
          Exception("⚠️ '${match.homeTeam} - ${match.awayTeam}' karşılaşması sona erdiği için kupon oynanamaz.")
        )
      }

      if (match.status == MatchStatus.LIVE && match.minute >= 85) {
        return Result.failure(
          Exception("⚠️ Kupon Kilitlendi: '${match.homeTeam} - ${match.awayTeam}' karşılaşmasında 85. dakika aşıldığı için canlı bahis alımı kapanmıştır.")
        )
      }

      if (match.status == MatchStatus.SCHEDULED) {
        val kickoff = getKickoffTimestamp(match)
        val closureTime = kickoff - CLOSURE_WINDOW_MS
        if (currentTimeMs >= closureTime) {
          val sdf = SimpleDateFormat("HH:mm", Locale.forLanguageTag("tr-TR")).apply { timeZone = turkeyTimeZone }
          val kickoffStr = sdf.format(Date(kickoff))
          val closureStr = sdf.format(Date(closureTime))
          return Result.failure(
            Exception(
              "⚠️ Kupon Kilitlendi: '${match.homeTeam} - ${match.awayTeam}' karşılaşması için bahis kapanış süresi dolmuştur!\n" +
                  "(Kapanış: $closureStr - Başlama: $kickoffStr, 5 Dakika Kuralı)"
            )
          )
        }
      }
    }

    return Result.success(Unit)
  }

  fun getClosureWindowLabel(match: Match): String {
    return when (val status = getClosureStatus(match)) {
      is BettingClosureStatus.Open -> "Kapanışa ${status.formattedRemaining}"
      is BettingClosureStatus.Locked5MinWindow -> "🔒 5 Dk Kuralı: Kilitlendi"
      is BettingClosureStatus.LiveInPlay -> "🔴 Canlı Bahis"
      is BettingClosureStatus.Finished -> "Bitti"
    }
  }
}
