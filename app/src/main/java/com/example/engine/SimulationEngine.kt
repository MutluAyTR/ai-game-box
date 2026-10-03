package com.example.engine

import android.content.Context
import com.example.data.datasource.MackolikFixtureGenerator
import com.example.data.model.EventType
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchEvent
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import com.example.service.CalendarManagementService
import com.example.service.LocalNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SimulationEngine(private val scope: CoroutineScope) {

  companion object {
    @Volatile
    var latestMatches: List<Match> = emptyList()
      internal set
  }

  private val _matches = MutableStateFlow<List<Match>>(emptyList())
  val matches: StateFlow<List<Match>> = _matches.asStateFlow()

  private val _lastSimulationMessage = MutableStateFlow<String?>(null)
  val lastSimulationMessage: StateFlow<String?> = _lastSimulationMessage.asStateFlow()

  private val _lastGoalAlert = MutableStateFlow<com.example.data.model.LiveGoalAlert?>(null)
  val lastGoalAlert: StateFlow<com.example.data.model.LiveGoalAlert?> = _lastGoalAlert.asStateFlow()

  private var appContext: Context? = null

  fun setApplicationContext(context: Context) {
    this.appContext = context.applicationContext
  }

  init {
    loadInitialFixtures()
    startLiveSimulation()
    scope.launch {
      _matches.collect { list ->
        if (list.isNotEmpty()) {
          latestMatches = list
        }
      }
    }
    // Real-time WebSocket frame listener for instant match finished and odds updates
    com.example.service.RealtimeSportsWebSocketService.onMatchFinishedListener = { matchId, finalHome, finalAway ->
      val currentList = _matches.value.toMutableList()
      val idx = currentList.indexOfFirst { it.id == matchId }
      if (idx != -1) {
        val match = currentList[idx]
        val ftEvents = match.events.toMutableList()
        if (ftEvents.none { it.description.contains("MAÇ BİTTİ") }) {
          ftEvents.add(
            0,
            MatchEvent(
              id = "ws_ft_${match.id}_${System.currentTimeMillis()}",
              minute = 90,
              type = EventType.QUARTER_END,
              team = "Hakem",
              player = match.referee,
              description = "🏁 MAÇ BİTTİ (MS: $finalHome - $finalAway) - WebSocket ile maç sonu onaylandı."
            )
          )
        }
        currentList[idx] = match.copy(
          status = MatchStatus.FINISHED,
          minute = 90,
          currentExtraMinute = 0,
          homeScore = finalHome,
          awayScore = finalAway,
          events = ftEvents
        )
        _matches.value = currentList
        _lastSimulationMessage.value = "🏁 [WebSocket] ${match.homeTeam} $finalHome - $finalAway ${match.awayTeam} maçı resmen bitti."
      }
    }

    com.example.service.RealtimeSportsWebSocketService.onMatchProgressListener = { matchId, min, extra, fin, hScore, aScore ->
      updateMatchLiveMinute(
        matchId = matchId,
        minute = min,
        extraMinute = extra,
        isFinished = fin,
        homeScore = hScore,
        awayScore = aScore
      )
    }

    com.example.service.RealtimeSportsWebSocketService.onOddsUpdateListener = { matchId, _, selection, newOdd ->
      val currentList = _matches.value.toMutableList()
      val idx = currentList.indexOfFirst { it.id == matchId }
      if (idx != -1) {
        val match = currentList[idx]
        val updatedMarkets = match.markets.map { market ->
          if (market.type == MarketType.MATCH_RESULT) {
            val updatedItems = market.selections.map { sel ->
              if (sel.name == selection || (selection == "1" && sel.name.contains("1")) || (selection == "X" && sel.name.contains("X")) || (selection == "2" && sel.name.contains("2"))) {
                sel.copy(
                  odd = newOdd,
                  previousOdd = sel.odd,
                  isOddsUp = newOdd > sel.odd,
                  isOddsDown = newOdd < sel.odd
                )
              } else sel
            }
            market.copy(selections = updatedItems)
          } else market
        }
        currentList[idx] = match.copy(markets = updatedMarkets)
        _matches.value = currentList
      }
    }
  }

  fun loadInitialFixtures() {
    val initial = MackolikFixtureGenerator.generateAllMatches().toMutableList()
    syncMatchesWithClock(initial, CalendarManagementService.getCurrentTimeMillis())
    _matches.value = initial
  }

  fun loadMatches(loaded: List<Match>) {
    if (loaded.isNotEmpty()) {
      val list = loaded.toMutableList()
      syncMatchesWithClock(list, CalendarManagementService.getCurrentTimeMillis())
      _matches.value = list
    }
  }

  fun addMatches(newMatches: List<Match>) {
    if (newMatches.isEmpty()) return
    val current = _matches.value.toMutableList()
    val existingIds = current.map { it.id }.toSet()
    val toAdd = newMatches.filter { it.id !in existingIds }
    current.addAll(0, toAdd)
    syncMatchesWithClock(current, CalendarManagementService.getCurrentTimeMillis())
    _matches.value = current
  }

  fun loadWeekFixtures(week: Int) {
    val weekMatches = MackolikWeeklyFixtureEngine.generateFixturesForWeek(week).toMutableList()
    syncMatchesWithClock(weekMatches, CalendarManagementService.getCurrentTimeMillis())
    val current = _matches.value.toMutableList()
    val existingIds = current.map { it.id }.toSet()
    val toAdd = weekMatches.filter { it.id !in existingIds }
    current.addAll(toAdd)
    _matches.value = current
  }

  /**
   * Synchronizes match state (SCHEDULED -> LIVE -> FINISHED) based on current simulated Turkey Time.
   * Ensures that when the clock reaches 19:00 TSİ, all 19:00 matches start automatically.
   */
  fun syncMatchesWithClock(
    list: MutableList<Match>,
    nowMs: Long = CalendarManagementService.getCurrentTimeMillis()
  ): Boolean {
    var modified = false
    for (i in list.indices) {
      val match = list[i]
      val kickoff = CalendarManagementService.getKickoffTimestamp(match)
      val elapsedMs = nowMs - kickoff

      val durationMs = when (match.sport) {
        Sport.FOOTBALL -> 100 * 60 * 1000L // 90 min + 10 min stoppage / breaks
        Sport.BASKETBALL -> 80 * 60 * 1000L
        Sport.TENNIS -> 120 * 60 * 1000L
        Sport.MOTORSPORTS -> 50 * 60 * 1000L
        else -> 90 * 60 * 1000L
      }

      if (elapsedMs < 0) {
        // Kickoff is in future: Match MUST be SCHEDULED
        if (match.status != MatchStatus.SCHEDULED && match.status != MatchStatus.UPCOMING) {
          list[i] = match.copy(
            status = MatchStatus.SCHEDULED,
            minute = 0,
            homeScore = 0,
            awayScore = 0
          )
          modified = true
        }
      } else if (elapsedMs <= durationMs) {
        // Match has started and is in play or extra time
        val totalMins = (elapsedMs / (60 * 1000L)).toInt()
        val calculatedMinute: Int
        val extraMin: Int
        var shouldFinishNow = false

        if (match.sport == Sport.FOOTBALL) {
          if (totalMins <= 45) {
            calculatedMinute = totalMins.coerceAtLeast(1)
            extraMin = 0
          } else if (totalMins in 46..55) {
            calculatedMinute = 45 // Devre Arası
            extraMin = 0
          } else if (totalMins in 56..94) {
            calculatedMinute = (45 + (totalMins - 55)).coerceIn(46, 90)
            extraMin = 0
          } else if (totalMins in 95..99) {
            calculatedMinute = 90
            extraMin = totalMins - 94 // 90+1, 90+2, 90+3, 90+4, 90+5
          } else {
            calculatedMinute = 90
            extraMin = 5
            shouldFinishNow = true
          }
        } else {
          val maxMin = when (match.sport) {
            Sport.BASKETBALL -> 40
            Sport.MOTORSPORTS -> 25
            else -> 90
          }
          val cur = ((elapsedMs.toDouble() / durationMs) * maxMin).toInt()
          if (cur >= maxMin) {
            calculatedMinute = maxMin
            extraMin = 0
            shouldFinishNow = true
          } else {
            calculatedMinute = cur.coerceAtLeast(1)
            extraMin = 0
          }
        }

        if (shouldFinishNow) {
          // Reached full-time end: Finish match!
          if (match.status != MatchStatus.FINISHED) {
            val ftEvents = match.events.toMutableList()
            if (ftEvents.none { it.description.contains("MAÇ BİTTİ") }) {
              ftEvents.add(
                0,
                MatchEvent(
                  id = "ft_${match.id}",
                  minute = calculatedMinute,
                  type = EventType.QUARTER_END,
                  team = "Hakem",
                  player = match.referee,
                  description = "🏁 MAÇ BİTTİ (MS: ${match.homeScore} - ${match.awayScore}) - Karşılaşma tamamlandı."
                )
              )
            }
            list[i] = match.copy(
              status = MatchStatus.FINISHED,
              minute = calculatedMinute,
              currentExtraMinute = extraMin,
              events = ftEvents
            )
            modified = true
          }
        } else {
          // In-play match: keep minute and extraMinute in sync!
          if (match.status != MatchStatus.LIVE || match.minute != calculatedMinute || match.currentExtraMinute != extraMin) {
            val kickoffEvents = if (match.events.isEmpty()) {
              listOf(
                MatchEvent(
                  id = "start_${match.id}",
                  minute = 1,
                  type = EventType.QUARTER_END,
                  team = "Hakem",
                  player = match.referee,
                  description = "⚽ Karşılaşma TSİ ${match.startTime}'da hakemin ilk düdüğüyle canlı başladı!"
                )
              )
            } else match.events

            list[i] = match.copy(
              status = MatchStatus.LIVE,
              minute = calculatedMinute,
              currentExtraMinute = extraMin,
              events = kickoffEvents,
              isHot = true
            )
            modified = true
          }
        }
      } else {
        // Elapsed time exceeds full match duration: Match MUST be FINISHED
        if (match.status != MatchStatus.FINISHED) {
          val ftEvents = match.events.toMutableList()
          if (ftEvents.none { it.description.contains("MAÇ BİTTİ") }) {
            ftEvents.add(
              0,
              MatchEvent(
                id = "ft_${match.id}",
                minute = if (match.sport == Sport.FOOTBALL) 90 else 40,
                type = EventType.QUARTER_END,
                team = "Hakem",
                player = match.referee,
                description = "🏁 MAÇ BİTTİ (MS: ${match.homeScore} - ${match.awayScore}) - Karşılaşma tamamlandı."
              )
            )
          }
          list[i] = match.copy(
            status = MatchStatus.FINISHED,
            minute = if (match.sport == Sport.FOOTBALL) 90 else 40,
            events = ftEvents
          )
          modified = true
        }
      }
    }
    return modified
  }

  fun syncAllMatchesWithCurrentTime(nowMs: Long = CalendarManagementService.getCurrentTimeMillis()) {
    val currentList = _matches.value.toMutableList()
    val updated = syncMatchesWithClock(currentList, nowMs)
    if (updated || _matches.value.isEmpty()) {
      _matches.value = currentList
    }
  }

  private fun startLiveSimulation() {
    scope.launch(Dispatchers.Default) {
      while (true) {
        delay(4000) // Every 4 seconds, tick matches forward
        advanceSimulationStep()
      }
    }
  }

  private fun advanceSimulationStep() {
    val currentList = _matches.value.toMutableList()
    val nowMs = CalendarManagementService.getCurrentTimeMillis()
    var updated = syncMatchesWithClock(currentList, nowMs)

    for (i in currentList.indices) {
      val match = currentList[i]
      if (match.status != MatchStatus.LIVE) continue

      // Increment minute / duration
      var nextMinute = match.minute
      var nextExtraMinute = match.currentExtraMinute
      var extraTimeMinutes = match.extraTimeMinutes
      var isFinished = false

      if (match.sport == Sport.FOOTBALL) {
        if (nextMinute < 90) {
          nextMinute++
          // 85th Minute Warning Trigger
          if (nextMinute == 85) {
            appContext?.let { ctx ->
              LocalNotificationManager.notifyBettingLocked(ctx, "${match.homeTeam} - ${match.awayTeam}")
            }
          }
          // When hitting 90, determine stoppage time
          if (nextMinute == 90) {
            val goalsCount = match.homeScore + match.awayScore
            val varCount = match.events.count { it.type == EventType.VAR_CHECK }
            val cardsCount = match.events.count { it.type == EventType.YELLOW_CARD || it.type == EventType.RED_CARD }
            val injuriesCount = match.events.count { it.type == EventType.INJURY }
            extraTimeMinutes = FairPlaySimulationEngine.calculateDynamicExtraTime(goalsCount, varCount, cardsCount, injuriesCount)
            _lastSimulationMessage.value = "⏱️ ${match.homeTeam} - ${match.awayTeam}: 4. Hakem +$extraTimeMinutes dakika ilave süre gösterdi!"
          }
        } else {
          // Already in 90th minute stoppage time
          nextExtraMinute++
          if (nextExtraMinute > extraTimeMinutes) {
            isFinished = true
          }
        }
      } else {
        nextMinute++
        if (nextMinute > 40 && match.sport == Sport.BASKETBALL) isFinished = true
        else if (nextMinute > 135 && match.sport == Sport.TENNIS) isFinished = true
        else if (nextMinute > 25 && match.sport == Sport.MOTORSPORTS) isFinished = true
        else if (nextMinute > 60 && match.sport == Sport.ICE_HOCKEY) isFinished = true
      }

      // Check Match Finish Conditions
      if (isFinished) {
        val finalEvents = match.events.toMutableList()
        finalEvents.add(
          0,
          MatchEvent(
            id = "ft_${System.currentTimeMillis()}",
            minute = if (match.sport == Sport.FOOTBALL) 90 else nextMinute,
            type = EventType.QUARTER_END,
            team = "Hakem",
            player = match.referee,
            description = "🏁 MAÇ BİTTİ (MS: ${match.homeScore} - ${match.awayScore}) - Hakem karşılaşmanın son düdüğünü çaldı!"
          )
        )
        currentList[i] = match.copy(
          status = MatchStatus.FINISHED,
          minute = if (match.sport == Sport.FOOTBALL) 90 else nextMinute,
          currentExtraMinute = extraTimeMinutes,
          events = finalEvents.take(30)
        )
        updated = true
        _lastSimulationMessage.value = "🏁 MAÇ SONUCU: ${match.homeTeam} ${match.homeScore} - ${match.awayScore} ${match.awayTeam} (Bitti)"
        promoteNextScheduledMatch(currentList)
        continue
      }

      var newHomeScore = match.homeScore
      var newAwayScore = match.awayScore
      val newEvents = match.events.toMutableList()
      var isVarCheck = false

      var updatedCornersHome = match.statistics.cornersHome
      var updatedCornersAway = match.statistics.cornersAway
      var updatedYellowHome = match.statistics.yellowCardsHome
      var updatedYellowAway = match.statistics.yellowCardsAway
      var updatedShotsHome = match.statistics.shotsHome
      var updatedShotsAway = match.statistics.shotsAway
      var updatedShotsOnTargetHome = match.statistics.shotsOnTargetHome
      var updatedShotsOnTargetAway = match.statistics.shotsOnTargetAway

      // Execute AI-Based Fair Play Logic Engine for football
      if (match.sport == Sport.FOOTBALL) {
        when (val fairEvent = FairPlaySimulationEngine.evaluateMinuteStep(match, nextMinute)) {
          is FairPlayEventResult.Goal -> {
            if (fairEvent.isHome) {
              newHomeScore++
              updatedShotsHome++
              updatedShotsOnTargetHome++
            } else {
              newAwayScore++
              updatedShotsAway++
              updatedShotsOnTargetAway++
            }
            newEvents.add(
              0,
              MatchEvent(
                id = "goal_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.GOAL,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = fairEvent.scorer,
                description = "${fairEvent.description} ($newHomeScore - $newAwayScore)"
              )
            )
            val goalAlert = com.example.data.model.LiveGoalAlert(
              matchId = match.id,
              homeTeam = match.homeTeam,
              awayTeam = match.awayTeam,
              scoringTeam = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
              scorerName = fairEvent.scorer,
              minute = nextMinute,
              homeScore = newHomeScore,
              awayScore = newAwayScore,
              rawMessage = "⚽ GOL! ${fairEvent.scorer} (${nextMinute}') - ${match.homeTeam} $newHomeScore - $newAwayScore ${match.awayTeam}"
            )
            _lastGoalAlert.value = goalAlert
            _lastSimulationMessage.value = goalAlert.rawMessage
          }
          is FairPlayEventResult.Injury -> {
            newEvents.add(
              0,
              MatchEvent(
                id = "inj_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.INJURY,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = fairEvent.injuredPlayer,
                description = fairEvent.description
              )
            )
            _lastSimulationMessage.value = "🚑 Sakatlık: ${fairEvent.injuredPlayer} (${if (fairEvent.isHome) match.homeTeam else match.awayTeam})"
          }
          is FairPlayEventResult.YellowCard -> {
            if (fairEvent.isHome) updatedYellowHome++ else updatedYellowAway++
            newEvents.add(
              0,
              MatchEvent(
                id = "card_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.YELLOW_CARD,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = fairEvent.player,
                description = fairEvent.reason
              )
            )
          }
          is FairPlayEventResult.RedCard -> {
            newEvents.add(
              0,
              MatchEvent(
                id = "rcard_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.RED_CARD,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = fairEvent.player,
                description = fairEvent.reason
              )
            )
            _lastSimulationMessage.value = "🟥 KIRMIZI KART! ${fairEvent.player} (${if (fairEvent.isHome) match.homeTeam else match.awayTeam})"
          }
          is FairPlayEventResult.VarCheck -> {
            isVarCheck = true
            newEvents.add(
              0,
              MatchEvent(
                id = "var_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.VAR_CHECK,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = "VAR Odası",
                description = fairEvent.description
              )
            )
            _lastSimulationMessage.value = "${match.homeTeam} - ${match.awayTeam}: ${fairEvent.description}"
          }
          is FairPlayEventResult.ShotOnTarget -> {
            if (fairEvent.isHome) {
              updatedShotsHome++
              updatedShotsOnTargetHome++
            } else {
              updatedShotsAway++
              updatedShotsOnTargetAway++
            }
            newEvents.add(
              0,
              MatchEvent(
                id = "shot_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.SHOT_ON_TARGET,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = fairEvent.player,
                description = if (fairEvent.wasSaved) "🧤 ${fairEvent.player} vurdu, kaleci köşeden çıkardı!" else "🎯 ${fairEvent.player} kaleyi yokladı!"
              )
            )
          }
          is FairPlayEventResult.Corner -> {
            if (fairEvent.isHome) updatedCornersHome++ else updatedCornersAway++
            newEvents.add(
              0,
              MatchEvent(
                id = "corn_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.CORNER,
                team = if (fairEvent.isHome) match.homeTeam else match.awayTeam,
                player = "",
                description = "🚩 Köşe vuruşu kullanıldı"
              )
            )
          }
          is FairPlayEventResult.NormalPlay -> {
            // Normal tactical play
          }
        }
      } else {
        val randomChance = Random.nextInt(100)
        if (match.sport == Sport.BASKETBALL && randomChance < 25) {
          val isHome = Random.nextBoolean()
          val points = if (Random.nextInt(10) < 3) 3 else 2
          if (isHome) newHomeScore += points else newAwayScore += points
          val basketTeam = if (isHome) match.homeTeam else match.awayTeam
          newEvents.add(
            0,
            MatchEvent(
              id = "b_${System.currentTimeMillis()}",
              minute = nextMinute,
              type = EventType.SHOT_ON_TARGET,
              team = basketTeam,
              player = "$points Sayılık Basket",
              description = "🏀 $basketTeam isabet buldu! ($newHomeScore - $newAwayScore)"
            )
          )
        } else if (match.sport == Sport.MOTORSPORTS) {
          if (newHomeScore == 0 && newAwayScore == 0) {
            val isWrc = match.league.contains("WRC", ignoreCase = true)
            if (isWrc) {
              newHomeScore = 25
              newAwayScore = 18
            } else {
              newHomeScore = 25
              newAwayScore = 20
            }
          }
          if (randomChance < 15) {
            val isOvertake = Random.nextInt(100) < 30
            if (isOvertake) {
              // Position swap between leader & chaser
              val temp = newHomeScore
              newHomeScore = newAwayScore
              newAwayScore = temp
              val newLeader = if (newHomeScore > newAwayScore) match.homeTeam else match.awayTeam
              newEvents.add(
                0,
                MatchEvent(
                  id = "m_${System.currentTimeMillis()}",
                  minute = nextMinute,
                  type = EventType.SHOT_ON_TARGET,
                  team = newLeader,
                  player = newLeader,
                  description = "🏎️ LİDERLİK DEĞİŞTİ! $newLeader harika bir atakla zirveye yerleşti (Tur $nextMinute)!"
                )
              )
            } else {
              val rider = if (Random.nextBoolean()) match.homeTeam else match.awayTeam
              newEvents.add(
                0,
                MatchEvent(
                  id = "m_${System.currentTimeMillis()}",
                  minute = nextMinute,
                  type = EventType.SHOT_ON_TARGET,
                  team = rider,
                  player = rider,
                  description = "🏎️ $rider en hızlı sektör derecesi yaptı! (Tur $nextMinute, Fark: +0.24s)"
                )
              )
            }
          }
        } else if (match.sport == Sport.TENNIS && randomChance < 12) {
          val isHome = Random.nextBoolean()
          if (newHomeScore < 2 && newAwayScore < 2) {
            if (isHome) newHomeScore++ else newAwayScore++
            val winner = if (isHome) match.homeTeam else match.awayTeam
            newEvents.add(
              0,
              MatchEvent(
                id = "t_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.SHOT_ON_TARGET,
                team = winner,
                player = winner,
                description = "🎾 SET! $winner seti hanesine yazdırdı! ($newHomeScore - $newAwayScore setler)"
              )
            )
          }
        } else if (match.sport == Sport.VOLLEYBALL && randomChance < 14) {
          val isHome = Random.nextBoolean()
          if (newHomeScore < 3 && newAwayScore < 3) {
            if (isHome) newHomeScore++ else newAwayScore++
            val winner = if (isHome) match.homeTeam else match.awayTeam
            newEvents.add(
              0,
              MatchEvent(
                id = "v_${System.currentTimeMillis()}",
                minute = nextMinute,
                type = EventType.SHOT_ON_TARGET,
                team = winner,
                player = winner,
                description = "🏐 SET! $winner seti kazandı ($newHomeScore - $newAwayScore)"
              )
            )
          }
        } else if (match.sport == Sport.ICE_HOCKEY && randomChance < 6) {
          val isHome = Random.nextBoolean()
          if (isHome) newHomeScore++ else newAwayScore++
          val team = if (isHome) match.homeTeam else match.awayTeam
          newEvents.add(
            0,
            MatchEvent(
              id = "h_${System.currentTimeMillis()}",
              minute = nextMinute,
              type = EventType.GOAL,
              team = team,
              player = "$team Forveti",
              description = "🏒 GOL! $team skoru değiştirdi! ($newHomeScore - $newAwayScore)"
            )
          )
        }
      }

      // Re-calculate live odds using the dynamic in-play OddsCalculationEngine
      val recalculatedMarkets = OddsCalculationEngine.recalculateLiveMarkets(
        match = match,
        homeScore = newHomeScore,
        awayScore = newAwayScore,
        minute = nextMinute,
        isVarCheck = isVarCheck
      )

      val updatedStats = match.statistics.copy(
        cornersHome = updatedCornersHome,
        cornersAway = updatedCornersAway,
        yellowCardsHome = updatedYellowHome,
        yellowCardsAway = updatedYellowAway,
        shotsHome = updatedShotsHome,
        shotsAway = updatedShotsAway,
        shotsOnTargetHome = updatedShotsOnTargetHome,
        shotsOnTargetAway = updatedShotsOnTargetAway
      )

      currentList[i] = match.copy(
        minute = nextMinute,
        homeScore = newHomeScore,
        awayScore = newAwayScore,
        events = newEvents.take(30),
        markets = recalculatedMarkets,
        statistics = updatedStats
      )
      updated = true
    }

    if (updated) {
      _matches.value = currentList
    }
  }

  /**
   * Synchronizes scheduled matches into LIVE state strictly based on their scheduled Turkey Time (TSİ) kickoff.
   * Matches will never start prematurely before their exact scheduled start time.
   */
  private fun promoteNextScheduledMatch(list: MutableList<Match>) {
    val nowMs = CalendarManagementService.getCurrentTimeMillis()
    syncMatchesWithClock(list, nowMs)
  }

  // --- Admin Panel Controls & Live Simulator Sync ---

  fun updateMatchLiveMinute(
    matchId: String,
    minute: Int,
    extraMinute: Int = 0,
    isFinished: Boolean = false,
    homeScore: Int? = null,
    awayScore: Int? = null
  ) {
    val currentList = _matches.value.toMutableList()
    val index = currentList.indexOfFirst { it.id == matchId }
    if (index != -1) {
      val match = currentList[index]
      val finalHome = homeScore ?: match.homeScore
      val finalAway = awayScore ?: match.awayScore
      val recalculatedMarkets = OddsCalculationEngine.recalculateLiveMarkets(
        match = match,
        homeScore = finalHome,
        awayScore = finalAway,
        minute = minute,
        isVarCheck = false
      )
      currentList[index] = match.copy(
        minute = minute,
        currentExtraMinute = extraMinute,
        status = if (isFinished) MatchStatus.FINISHED else MatchStatus.LIVE,
        homeScore = finalHome,
        awayScore = finalAway,
        markets = recalculatedMarkets
      )
      _matches.value = currentList
    }
  }

  fun updateMatchScore(matchId: String, newHome: Int, newAway: Int) {
    val currentList = _matches.value.toMutableList()
    val index = currentList.indexOfFirst { it.id == matchId }
    if (index != -1) {
      val match = currentList[index]
      val recalculatedMarkets = OddsCalculationEngine.recalculateLiveMarkets(
        match = match,
        homeScore = newHome,
        awayScore = newAway,
        minute = match.minute,
        isVarCheck = false
      )
      currentList[index] = match.copy(
        homeScore = newHome,
        awayScore = newAway,
        markets = recalculatedMarkets
      )
      _matches.value = currentList
      _lastSimulationMessage.value = "Admin: ${match.homeTeam} $newHome - $newAway ${match.awayTeam} olarak güncellendi."
    }
  }

  fun updateMatchMinute(matchId: String, minute: Int) {
    val currentList = _matches.value.toMutableList()
    val index = currentList.indexOfFirst { it.id == matchId }
    if (index != -1) {
      val match = currentList[index]
      val recalculatedMarkets = OddsCalculationEngine.recalculateLiveMarkets(
        match = match,
        homeScore = match.homeScore,
        awayScore = match.awayScore,
        minute = minute,
        isVarCheck = false
      )
      currentList[index] = match.copy(
        minute = minute,
        markets = recalculatedMarkets
      )
      _matches.value = currentList

      if (minute >= 85) {
        appContext?.let { ctx ->
          LocalNotificationManager.notifyBettingLocked(ctx, "${match.homeTeam} - ${match.awayTeam}")
        }
      }
    }
  }

  fun endMatch(matchId: String) {
    val currentList = _matches.value.toMutableList()
    val index = currentList.indexOfFirst { it.id == matchId }
    if (index != -1) {
      val match = currentList[index]
      currentList[index] = match.copy(
        status = MatchStatus.FINISHED,
        minute = 90
      )
      // Switch live derby / promote next scheduled match
      promoteNextScheduledMatch(currentList)
      _matches.value = currentList
      _lastSimulationMessage.value = "🏁 Maç Bitti: ${match.homeTeam} ${match.homeScore} - ${match.awayScore} ${match.awayTeam}"
    }
  }

  fun startNextLiveMatch() {
    val currentList = _matches.value.toMutableList()
    val nextIdx = currentList.indexOfFirst { it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.UPCOMING }
    if (nextIdx != -1) {
      val scheduled = currentList[nextIdx]
      currentList[nextIdx] = scheduled.copy(
        status = MatchStatus.LIVE,
        minute = 1,
        isHot = true
      )
      _matches.value = currentList
      _lastSimulationMessage.value = "⚡ Yeni Canlı Maç Başlatıldı: ${scheduled.homeTeam} - ${scheduled.awayTeam}"
    }
  }

  fun addNewMatch(match: Match) {
    val currentList = _matches.value.toMutableList()
    currentList.add(0, match)
    _matches.value = currentList
  }

  fun addNewMatch(home: String, away: String, league: String, sport: Sport, isHot: Boolean) {
    val id = "manual_${System.currentTimeMillis()}"
    val markets = MackolikFixtureGenerator.generateNesineMarkets(
      matchId = id,
      ms1 = 2.10,
      msX = 3.20,
      ms2 = 2.80,
      alt25 = 1.85,
      ust25 = 1.75,
      kgVar = 1.70,
      kgYok = 1.90
    )
    val match = Match(
      id = id,
      homeTeam = home,
      awayTeam = away,
      league = league,
      sport = sport,
      status = MatchStatus.SCHEDULED,
      startTime = "Bugün 20:00",
      homeScore = 0,
      awayScore = 0,
      minute = 0,
      isHot = isHot,
      markets = markets
    )
    addNewMatch(match)
  }

  fun resetFixtures() {
    loadInitialFixtures()
    _lastSimulationMessage.value = "Fikstür ve maçlar sıfırlandı."
  }

  fun triggerGoalForMatch(matchId: String, isHome: Boolean) {
    val currentList = _matches.value.toMutableList()
    val index = currentList.indexOfFirst { it.id == matchId }
    if (index != -1) {
      val match = currentList[index]
      val newHome = if (isHome) match.homeScore + 1 else match.homeScore
      val newAway = if (!isHome) match.awayScore + 1 else match.awayScore
      val scoringTeam = if (isHome) match.homeTeam else match.awayTeam
      val scorerName = MackolikFixtureGenerator.getScorerForTeam(scoringTeam)

      val newEvents = match.events.toMutableList()
      newEvents.add(
        0,
        MatchEvent(
          id = "man_goal_${System.currentTimeMillis()}",
          minute = match.minute,
          type = EventType.GOAL,
          team = scoringTeam,
          player = scorerName,
          description = "⚽ GOL! $scorerName (${match.minute}') - $scoringTeam skoru değiştirdi! ($newHome - $newAway)"
        )
      )

      val recalculatedMarkets = OddsCalculationEngine.recalculateLiveMarkets(
        match = match,
        homeScore = newHome,
        awayScore = newAway,
        minute = match.minute,
        isVarCheck = false
      )

      currentList[index] = match.copy(
        homeScore = newHome,
        awayScore = newAway,
        events = newEvents,
        markets = recalculatedMarkets
      )
      _matches.value = currentList
      val goalAlert = com.example.data.model.LiveGoalAlert(
        matchId = match.id,
        homeTeam = match.homeTeam,
        awayTeam = match.awayTeam,
        scoringTeam = if (isHome) match.homeTeam else match.awayTeam,
        scorerName = scorerName,
        minute = match.minute,
        homeScore = newHome,
        awayScore = newAway,
        rawMessage = "⚽ GOL! $scorerName (${match.minute}') - ${match.homeTeam} $newHome - $newAway ${match.awayTeam}"
      )
      _lastGoalAlert.value = goalAlert
      _lastSimulationMessage.value = goalAlert.rawMessage
    }
  }

  fun clearSimulationMessage() {
    _lastSimulationMessage.value = null
    _lastGoalAlert.value = null
  }
}
