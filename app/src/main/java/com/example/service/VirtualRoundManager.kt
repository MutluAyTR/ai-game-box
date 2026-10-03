package com.example.service

import com.example.data.model.MarketType
import com.example.data.model.Selection
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class VirtualMatchItem(
  val id: String,
  val homeTeam: String,
  val awayTeam: String,
  val sport: Sport,
  val league: String,
  var homeScore: Int = 0,
  var awayScore: Int = 0,
  var minute: Int = 0,
  val odd1: Double,
  val oddX: Double,
  val odd2: Double,
  val oddUnder: Double = 1.85,
  val oddOver: Double = 1.85,
  var isFinished: Boolean = false,
  var latestEvent: String = ""
)

enum class RoundPhase {
  BETTING_OPEN,    // 20s: Users place bets before kickoff
  MATCHES_IN_PLAY, // 25s: 1' to 90' simultaneous live updates across all 22 matches
  SETTLEMENT       // 5s: Winners celebrated, winnings credited, new round generated
}

enum class SlipItemLiveStatus {
  PENDING,            // Kickoff not started yet
  CURRENTLY_WINNING,  // Score currently fulfills the pick
  CURRENTLY_LOSING,   // Score currently doesn't fulfill the pick
  WON,                // Match finished and won
  LOST                // Match finished and lost
}

data class LiveCouponItemStatus(
  val slipSelection: SlipSelection,
  val status: SlipItemLiveStatus,
  val currentScore: String,
  val currentMinute: Int,
  val homeTeam: String,
  val awayTeam: String
)

data class VirtualRoundState(
  val roundNumber: Int = 28,
  val phase: RoundPhase = RoundPhase.BETTING_OPEN,
  val secondsRemaining: Int = 20,
  val matches: List<VirtualMatchItem> = emptyList(),
  val activeSlip: List<SlipSelection> = emptyList(),
  val placedTicketStake: Long = 0L,
  val lastRoundPayout: Long = 0L,
  val lastRoundWon: Boolean = false,
  val liveSlipItems: List<LiveCouponItemStatus> = emptyList(),
  val liveWinningCount: Int = 0,
  val liveLosingCount: Int = 0,
  val liveWinningPercentage: Int = 100,
  val liveEventsFeed: List<String> = emptyList()
)

/**
 * Real-time Data Polling Engine for Synchronized Virtual Sports:
 * Continuously polls and updates match timers and scores simultaneously across all 22 active fixtures,
 * keeping betting coupon status and live winning probability 100% in sync with simulated game progress.
 */
object VirtualRoundManager {

  private val scope = CoroutineScope(Dispatchers.Default)
  private val _state = MutableStateFlow(VirtualRoundState())
  val state: StateFlow<VirtualRoundState> = _state.asStateFlow()

  // Callback to credit winnings to balance
  var onCreditWinnings: ((Long, String) -> Unit)? = null

  init {
    startNewRound(28)
    startLoop()
  }

  private fun generateMatches(round: Int): List<VirtualMatchItem> {
    val list = mutableListOf<VirtualMatchItem>()

    // 12 Football Matches (Süper Lig + Premier Lig + La Liga)
    val footballFixtures = listOf(
      "Galatasaray" to "Fenerbahçe",
      "Beşiktaş" to "Trabzonspor",
      "Başakşehir" to "Samsunspor",
      "Göztepe" to "Kasımpaşa",
      "Antalyaspor" to "Sivasspor",
      "Alanyaspor" to "Rizespor",
      "Manchester City" to "Arsenal",
      "Liverpool" to "Chelsea",
      "Real Madrid" to "Barcelona",
      "Bayern Münih" to "Dortmund",
      "Inter" to "Juventus",
      "PSG" to "Marsilya"
    )

    footballFixtures.forEachIndexed { i, (home, away) ->
      val league = if (i < 6) "Süper Lig Sanal" else "Avrupa Devleri"
      val o1 = (Random.nextDouble(1.40, 2.90) * 100).toInt() / 100.0
      val oX = (Random.nextDouble(2.90, 3.60) * 100).toInt() / 100.0
      val o2 = (Random.nextDouble(2.10, 4.50) * 100).toInt() / 100.0
      list.add(
        VirtualMatchItem(
          id = "vm_fb_$i",
          homeTeam = home,
          awayTeam = away,
          sport = Sport.FOOTBALL,
          league = league,
          odd1 = o1,
          oddX = oX,
          odd2 = o2
        )
      )
    }

    // 10 Basketball Matches (EuroLeague + NBA)
    val basketballFixtures = listOf(
      "Fenerbahçe Beko" to "Anadolu Efes",
      "Real Madrid" to "Panathinaikos",
      "Olympiacos" to "Monaco",
      "Barcelona Basket" to "Milano",
      "Partizan" to "Kızılyıldız",
      "Boston Celtics" to "LA Lakers",
      "Golden State" to "Dallas Mavericks",
      "Denver Nuggets" to "Miami Heat",
      "Milwaukee Bucks" to "Philadelphia 76ers",
      "Phoenix Suns" to "LA Clippers"
    )

    basketballFixtures.forEachIndexed { i, (home, away) ->
      val league = if (i < 5) "EuroLeague Sanal" else "NBA Sanal"
      val o1 = (Random.nextDouble(1.45, 2.40) * 100).toInt() / 100.0
      val o2 = (Random.nextDouble(1.50, 2.60) * 100).toInt() / 100.0
      list.add(
        VirtualMatchItem(
          id = "vm_bb_$i",
          homeTeam = home,
          awayTeam = away,
          sport = Sport.BASKETBALL,
          league = league,
          odd1 = o1,
          oddX = 14.00,
          odd2 = o2,
          oddUnder = 1.88,
          oddOver = 1.88
        )
      )
    }

    return list
  }

  private fun startNewRound(round: Int) {
    _state.value = _state.value.copy(
      roundNumber = round,
      phase = RoundPhase.BETTING_OPEN,
      secondsRemaining = 20,
      matches = generateMatches(round),
      activeSlip = emptyList(),
      placedTicketStake = 0L,
      lastRoundPayout = 0L,
      lastRoundWon = false,
      liveSlipItems = emptyList(),
      liveWinningCount = 0,
      liveLosingCount = 0,
      liveWinningPercentage = 100,
      liveEventsFeed = listOf("Hafta $round bülteni açıldı. Kuponunuzu oluşturup onaylayın.")
    )
  }

  private fun computeSlipLiveStatuses(
    activeSlip: List<SlipSelection>,
    matches: List<VirtualMatchItem>,
    isFinal: Boolean
  ): List<LiveCouponItemStatus> {
    return activeSlip.map { sel ->
      val match = matches.find { it.id == sel.matchId }
      val hScore = match?.homeScore ?: 0
      val aScore = match?.awayScore ?: 0
      val minute = match?.minute ?: 0
      val home = match?.homeTeam ?: "Ev"
      val away = match?.awayTeam ?: "Dep"

      val isWinning = when {
        sel.selectionName.startsWith("MS 1") -> hScore > aScore
        sel.selectionName.startsWith("MS 2") -> aScore > hScore
        sel.selectionName.startsWith("MS X") -> hScore == aScore
        sel.selectionName.startsWith("2.5 Üst") -> (hScore + aScore) >= 3
        sel.selectionName.startsWith("2.5 Alt") -> (hScore + aScore) < 3
        sel.selectionName.startsWith("KG Var") -> hScore > 0 && aScore > 0
        else -> hScore > aScore
      }

      val status = when {
        isFinal && isWinning -> SlipItemLiveStatus.WON
        isFinal && !isWinning -> SlipItemLiveStatus.LOST
        isWinning -> SlipItemLiveStatus.CURRENTLY_WINNING
        else -> SlipItemLiveStatus.CURRENTLY_LOSING
      }

      LiveCouponItemStatus(
        slipSelection = sel,
        status = status,
        currentScore = "$hScore - $aScore",
        currentMinute = minute,
        homeTeam = home,
        awayTeam = away
      )
    }
  }

  private fun startLoop() {
    scope.launch {
      while (true) {
        delay(1000L) // 1000ms real-time polling frequency
        val cur = _state.value
        val sec = cur.secondsRemaining - 1

        if (sec > 0) {
          if (cur.phase == RoundPhase.MATCHES_IN_PLAY) {
            val newEvents = cur.liveEventsFeed.toMutableList()
            // Simultaneous progress across all 22 matches
            val simMatches = cur.matches.map { match ->
              val newMin = (match.minute + 4).coerceAtMost(90)
              val goalChance = Random.nextDouble()
              var newH = match.homeScore
              var newA = match.awayScore
              var eventTxt = match.latestEvent

              if (match.sport == Sport.FOOTBALL) {
                if (goalChance < 0.10) {
                  newH++
                  eventTxt = "⚽ $newMin' GOL! ${match.homeTeam} $newH - $newA ${match.awayTeam}"
                  newEvents.add(0, eventTxt)
                } else if (goalChance > 0.90) {
                  newA++
                  eventTxt = "⚽ $newMin' GOL! ${match.homeTeam} $newH - $newA ${match.awayTeam}"
                  newEvents.add(0, eventTxt)
                }
              } else {
                // Basketball points
                val hInc = Random.nextInt(2, 5)
                val aInc = Random.nextInt(2, 5)
                newH += hInc
                newA += aInc
                if (Random.nextDouble() < 0.15) {
                  newEvents.add(0, "🏀 $newMin' 3'lük basket! ${match.homeTeam} $newH - $newA ${match.awayTeam}")
                }
              }
              match.copy(minute = newMin, homeScore = newH, awayScore = newA, latestEvent = eventTxt)
            }

            // Sync betting coupon status in real-time
            val slipStatuses = computeSlipLiveStatuses(cur.activeSlip, simMatches, isFinal = false)
            val winCount = slipStatuses.count { it.status == SlipItemLiveStatus.CURRENTLY_WINNING }
            val loseCount = slipStatuses.count { it.status == SlipItemLiveStatus.CURRENTLY_LOSING }
            val winPct = if (slipStatuses.isNotEmpty()) ((winCount.toDouble() / slipStatuses.size) * 100).toInt() else 100

            _state.value = cur.copy(
              secondsRemaining = sec,
              matches = simMatches,
              liveSlipItems = slipStatuses,
              liveWinningCount = winCount,
              liveLosingCount = loseCount,
              liveWinningPercentage = winPct,
              liveEventsFeed = newEvents.take(20)
            )
          } else {
            _state.value = cur.copy(secondsRemaining = sec)
          }
        } else {
          // Transition to next phase
          when (cur.phase) {
            RoundPhase.BETTING_OPEN -> {
              // Initialize live coupon tracking
              val initialStatuses = computeSlipLiveStatuses(cur.activeSlip, cur.matches, isFinal = false)
              _state.value = cur.copy(
                phase = RoundPhase.MATCHES_IN_PLAY,
                secondsRemaining = 25,
                liveSlipItems = initialStatuses,
                liveEventsFeed = listOf("🏁 22 Maçın tamamı eş zamanlı başladı! Anlık skorlar akıyor...")
              )
            }
            RoundPhase.MATCHES_IN_PLAY -> {
              // Finish all matches at 90'
              val finished = cur.matches.map { it.copy(isFinished = true, minute = 90) }
              val finalSlipStatuses = computeSlipLiveStatuses(cur.activeSlip, finished, isFinal = true)

              // Settle bets
              var totalWin = 0L
              var wonAll = false
              if (cur.activeSlip.isNotEmpty() && cur.placedTicketStake > 0L) {
                var allCorrect = true
                var cumOdds = 1.0
                for (sel in cur.activeSlip) {
                  val m = finished.find { it.id == sel.matchId }
                  if (m != null) {
                    val outcome = when {
                      m.homeScore > m.awayScore -> "MS 1"
                      m.homeScore < m.awayScore -> "MS 2"
                      else -> "MS X"
                    }
                    if (sel.selectionName.startsWith(outcome)) {
                      cumOdds *= sel.odd
                    } else {
                      allCorrect = false
                    }
                  }
                }
                if (allCorrect) {
                  totalWin = (cur.placedTicketStake * cumOdds).toLong()
                  wonAll = true
                  onCreditWinnings?.invoke(totalWin, "Sanal 22 Maç Kuponu (${cur.activeSlip.size} Maç)")
                }
              }

              val finalWinCount = finalSlipStatuses.count { it.status == SlipItemLiveStatus.WON }
              val finalLoseCount = finalSlipStatuses.count { it.status == SlipItemLiveStatus.LOST }
              val finalWinPct = if (wonAll) 100 else 0

              _state.value = cur.copy(
                phase = RoundPhase.SETTLEMENT,
                secondsRemaining = 5,
                matches = finished,
                lastRoundPayout = totalWin,
                lastRoundWon = wonAll,
                liveSlipItems = finalSlipStatuses,
                liveWinningCount = finalWinCount,
                liveLosingCount = finalLoseCount,
                liveWinningPercentage = finalWinPct,
                liveEventsFeed = listOf(
                  if (wonAll) "🏆 TEBRİKLER! Kuponunuzdaki tüm maçlar tuttu: +$totalWin TP kazandınız!"
                  else "🏁 Maçlar sonuçlandı. Kupon tamamlandı. Yeni haftaya geçiliyor..."
                )
              )
            }
            RoundPhase.SETTLEMENT -> {
              startNewRound(cur.roundNumber + 1)
            }
          }
        }
      }
    }
  }

  fun toggleSlipSelection(selection: SlipSelection) {
    if (_state.value.phase != RoundPhase.BETTING_OPEN) return
    val cur = _state.value.activeSlip.toMutableList()
    val existingIndex = cur.indexOfFirst { it.matchId == selection.matchId }
    if (existingIndex >= 0) {
      if (cur[existingIndex].selectionName == selection.selectionName) {
        cur.removeAt(existingIndex)
      } else {
        cur[existingIndex] = selection
      }
    } else {
      cur.add(selection)
    }
    _state.value = _state.value.copy(activeSlip = cur)
  }

  fun placeRoundBet(stake: Long): Boolean {
    if (_state.value.phase != RoundPhase.BETTING_OPEN) return false
    if (_state.value.activeSlip.isEmpty()) return false
    _state.value = _state.value.copy(placedTicketStake = stake)
    return true
  }

  fun clearSlip() {
    _state.value = _state.value.copy(activeSlip = emptyList(), placedTicketStake = 0L)
  }
}
