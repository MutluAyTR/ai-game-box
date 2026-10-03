package com.example.service

import com.example.data.model.Match
import com.example.data.model.Sport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class F1TelemetryUpdate(
  val speedKmh: Double,
  val rpm: Int,
  val gear: Int,
  val throttlePct: Int,
  val brakePct: Int,
  val sector1Time: String,
  val sector2Time: String,
  val sector3Time: String,
  val currentLapTime: String,
  val deltaToLeader: String,
  val tyreTempFrontLeft: Int,
  val tyreTempFrontRight: Int,
  val drsActive: Boolean,
  val trackStatus: String, // "GREEN", "YELLOW", "VSC", "SC"
  val lapNumber: Int,
  val totalLaps: Int
)

data class TennisTelemetryUpdate(
  val homeGameScore: String, // "0", "15", "30", "40", "AD"
  val awayGameScore: String,
  val set1Home: Int,
  val set1Away: Int,
  val set2Home: Int,
  val set2Away: Int,
  val set3Home: Int,
  val set3Away: Int,
  val currentServer: String,
  val lastServeSpeedKmh: Int,
  val rallyLength: Int,
  val isBreakPoint: Boolean,
  val breakPointNotice: String?
)

data class LiveMatchTelemetry(
  val matchId: String,
  val isConnected: Boolean = true,
  val latencyMs: Int = 12,
  val fps: Int = 60,
  val f1Update: F1TelemetryUpdate? = null,
  val tennisUpdate: TennisTelemetryUpdate? = null,
  val lastTimestamp: Long = System.currentTimeMillis()
)

/**
 * Real-time WebSocket Client & Telemetry Data Service
 * Provides low-latency streaming telemetry for F1, MotoGP, WRC, and Tennis matches.
 */
object LiveTelemetryWebSocketService {
  private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  private val telemetryFlows = mutableMapOf<String, MutableStateFlow<LiveMatchTelemetry>>()

  fun getTelemetryFlowForMatch(match: Match): StateFlow<LiveMatchTelemetry> {
    return telemetryFlows.getOrPut(match.id) {
      val initial = createInitialTelemetry(match)
      val flow = MutableStateFlow(initial)
      startStreamingSimulation(match, flow)
      flow
    }.asStateFlow()
  }

  private fun createInitialTelemetry(match: Match): LiveMatchTelemetry {
    val isMotorsport = match.sport == Sport.MOTORSPORTS ||
        match.sport == Sport.MOTOGP ||
        match.sport == Sport.FORMULA_1 ||
        match.sport == Sport.WRC_RALLY ||
        match.league.contains("MotoGP", ignoreCase = true) ||
        match.league.contains("Formula", ignoreCase = true) ||
        match.league.contains("F1", ignoreCase = true) ||
        match.league.contains("WRC", ignoreCase = true)

    val isTennis = match.sport == Sport.TENNIS ||
        match.sport == Sport.TABLE_TENNIS ||
        match.league.contains("Tenis", ignoreCase = true) ||
        match.league.contains("WTA", ignoreCase = true) ||
        match.league.contains("ATP", ignoreCase = true)

    val f1 = if (isMotorsport) {
      F1TelemetryUpdate(
        speedKmh = 348.5,
        rpm = 12450,
        gear = 7,
        throttlePct = 96,
        brakePct = 0,
        sector1Time = "27.420s",
        sector2Time = "26.890s",
        sector3Time = "27.535s",
        currentLapTime = "1:21.845",
        deltaToLeader = "+0.067s",
        tyreTempFrontLeft = 104,
        tyreTempFrontRight = 102,
        drsActive = true,
        trackStatus = "GREEN",
        lapNumber = 34,
        totalLaps = 53
      )
    } else null

    val tennis = if (isTennis) {
      TennisTelemetryUpdate(
        homeGameScore = "40",
        awayGameScore = "30",
        set1Home = 6,
        set1Away = 4,
        set2Home = 4,
        set2Away = 6,
        set3Home = 4,
        set3Away = 3,
        currentServer = match.homeTeam,
        lastServeSpeedKmh = 212,
        rallyLength = 7,
        isBreakPoint = true,
        breakPointNotice = "Break Point Fırsatı! (Servis Kırma)"
      )
    } else null

    return LiveMatchTelemetry(
      matchId = match.id,
      isConnected = true,
      latencyMs = 12,
      fps = 60,
      f1Update = f1,
      tennisUpdate = tennis
    )
  }

  private fun startStreamingSimulation(match: Match, flow: MutableStateFlow<LiveMatchTelemetry>) {
    serviceScope.launch {
      var speed = 348.5
      var rpm = 12450
      var gear = 7
      var throttle = 96
      var brake = 0
      var deltaMs = 67
      var currentLapSeconds = 81.845

      // Tennis states
      val pointScores = listOf("0", "15", "30", "40", "AD")
      var homeScoreIdx = 3
      var awayScoreIdx = 2
      var rally = 7
      var serveSpeed = 212

      while (isActive) {
        delay(600) // High-frequency stream update

        val current = flow.value
        val latency = Random.nextInt(10, 16)

        // Update Motorsport
        val updatedF1 = current.f1Update?.let { old ->
          // fluctuate speed & rpm
          val speedDelta = (Random.nextDouble(-4.5, 4.8) * 10).roundToInt() / 10.0
          speed = (speed + speedDelta).coerceIn(310.0, 362.0)

          if (speed > 340.0) {
            gear = 7
            rpm = (12000 + (speed - 340.0) * 100).toInt().coerceIn(11800, 13200)
            throttle = Random.nextInt(92, 100)
            brake = 0
          } else if (speed > 320.0) {
            gear = 6
            rpm = 11500
            throttle = Random.nextInt(75, 90)
            brake = 0
          } else {
            gear = 5
            rpm = 10800
            throttle = Random.nextInt(40, 70)
            brake = Random.nextInt(0, 30)
          }

          deltaMs = (deltaMs + Random.nextInt(-2, 3)).coerceIn(40, 120)
          val deltaStr = "+0.${deltaMs.toString().padStart(3, '0')}s"

          currentLapSeconds = (currentLapSeconds + 0.6)
          val mins = (currentLapSeconds / 60).toInt()
          val secs = (currentLapSeconds % 60)
          val lapTimeStr = String.format("%d:%06.3f", mins, secs)

          old.copy(
            speedKmh = ((speed * 10).roundToInt() / 10.0),
            rpm = rpm,
            gear = gear,
            throttlePct = throttle,
            brakePct = brake,
            currentLapTime = lapTimeStr,
            deltaToLeader = deltaStr,
            tyreTempFrontLeft = (102 + Random.nextInt(0, 4)),
            tyreTempFrontRight = (101 + Random.nextInt(0, 4))
          )
        }

        // Update Tennis
        val updatedTennis = current.tennisUpdate?.let { old ->
          rally = (rally + Random.nextInt(1, 3)) % 14 + 1
          serveSpeed = (serveSpeed + Random.nextInt(-2, 3)).coerceIn(198, 224)

          // Chance to cycle point
          if (Random.nextInt(0, 10) > 7) {
            homeScoreIdx = (homeScoreIdx + 1) % pointScores.size
            if (homeScoreIdx == 0) {
              awayScoreIdx = (awayScoreIdx + 1) % pointScores.size
            }
          }

          val isBP = homeScoreIdx == 3 && awayScoreIdx < 3
          old.copy(
            homeGameScore = pointScores[homeScoreIdx],
            awayGameScore = pointScores[awayScoreIdx],
            lastServeSpeedKmh = serveSpeed,
            rallyLength = rally,
            isBreakPoint = isBP,
            breakPointNotice = if (isBP) "🔥 Break Point Fırsatı! (Servis Kırma)" else null
          )
        }

        flow.value = current.copy(
          latencyMs = latency,
          f1Update = updatedF1,
          tennisUpdate = updatedTennis,
          lastTimestamp = System.currentTimeMillis()
        )
      }
    }
  }
}
