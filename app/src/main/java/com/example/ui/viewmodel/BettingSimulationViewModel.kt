package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BetEntry
import com.example.data.local.Match
import com.example.data.local.TicketEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * UI State for Bet Placement operations
 */
sealed interface BetPlacementState {
  data object Idle : BetPlacementState
  data object Loading : BetPlacementState
  data class Success(
    val betId: String,
    val newBalance: Long,
    val potentialReturn: Long,
    val message: String
  ) : BetPlacementState
  data class Error(val message: String) : BetPlacementState
}

/**
 * ViewModel that handles the logic for placing bets using virtual tokens (TP),
 * including strict validation to ensure:
 * 1. The user has enough balance.
 * 2. The selected match is still active (LIVE, UPCOMING, SCHEDULED) and not FINISHED.
 * Persists match data, tickets, and user balances using Room Database.
 */
class BettingSimulationViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val simulatedMatchDao = database.simulatedMatchDao()
  private val basketballMatchDao = database.basketballMatchDao()
  private val tennisMatchDao = database.tennisMatchDao()
  private val formula1RaceDao = database.formula1RaceDao()
  private val walletDao = database.walletDao()
  private val userBalanceDao = database.userBalanceDao()
  private val betEntryDao = database.betEntryDao()
  private val ticketDao = database.ticketDao()

  // Gemini Service for Room database match history analysis & win probability
  private val geminiProbabilityService = com.example.service.GeminiMatchHistoryProbabilityService(database)

  // Real-time Virtual Token (TP) balance flow from Room
  val virtualTokenBalance: StateFlow<Long> = walletDao.getWalletFlow()
    .map { it?.availablePoints ?: 10000L }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000L)

  // Real-time active matches flow (live & upcoming) from Room
  val activeMatches: StateFlow<List<Match>> = simulatedMatchDao.getActiveMatchesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Real-time live matches
  val liveMatches: StateFlow<List<Match>> = simulatedMatchDao.getLiveMatchesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Real-time upcoming matches
  val upcomingMatches: StateFlow<List<Match>> = simulatedMatchDao.getUpcomingMatchesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Specialized Basketball Matches (periods, rebounds, quarter scores)
  val basketballMatches: StateFlow<List<com.example.data.local.BasketballMatchEntity>> =
    basketballMatchDao.getActiveMatchesFlow()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Specialized Tennis Matches (sets, games, tie-breaks, aces)
  val tennisMatches: StateFlow<List<com.example.data.local.TennisMatchEntity>> =
    tennisMatchDao.getActiveMatchesFlow()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Specialized Formula 1 Races (pit-stops, lap telemetry, tyre compounds)
  val formula1Races: StateFlow<List<com.example.data.local.Formula1RaceEntity>> =
    formula1RaceDao.getActiveRacesFlow()
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Bet history flow
  val betHistory: StateFlow<List<BetEntry>> = betEntryDao.getBetEntriesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Gemini Win Probability State
  private val _winProbabilityAnalysis = MutableStateFlow<com.example.service.WinProbabilityAnalysis?>(null)
  val winProbabilityAnalysis: StateFlow<com.example.service.WinProbabilityAnalysis?> = _winProbabilityAnalysis.asStateFlow()

  private val _isAnalyzingProbability = MutableStateFlow(false)
  val isAnalyzingProbability: StateFlow<Boolean> = _isAnalyzingProbability.asStateFlow()

  private val _betPlacementState = MutableStateFlow<BetPlacementState>(BetPlacementState.Idle)
  val betPlacementState: StateFlow<BetPlacementState> = _betPlacementState.asStateFlow()

  init {
    ensureInitialWalletAndMatches()
  }

  /**
   * Initializes starter wallet and simulated matches across sports if table is empty.
   */
  private fun ensureInitialWalletAndMatches() {
    viewModelScope.launch(Dispatchers.IO) {
      // 1. Initialize Wallet if null
      val wallet = walletDao.getWallet()
      if (wallet == null) {
        walletDao.insertOrUpdateWallet(
          WalletEntity(
            id = 1,
            availablePoints = 10000L,
            lockedPoints = 0L,
            lifetimeWon = 0L,
            lifetimeLost = 0L
          )
        )
        walletDao.insertTransaction(
          TransactionEntity(
            type = "WELCOME_BONUS",
            amount = 10000L,
            balanceAfter = 10000L,
            description = "Tahmin Arena Sanal TP Başlangıç Bakiyesi 🎁",
            createdAt = System.currentTimeMillis()
          )
        )
      }

      // 2. Initialize starter matches if empty
      val existingMatches = simulatedMatchDao.getAllMatches()
      if (existingMatches.isEmpty()) {
        seedSampleSimulatedMatches()
      }
    }
  }

  /**
   * Validates and places a bet on a single match using virtual tokens.
   *
   * @param matchId ID of the match to bet on
   * @param selection Name of the chosen outcome (e.g. "MS 1", "2.5 Üst")
   * @param odds The market odd (e.g. 1.85)
   * @param stakeAmount The amount of virtual tokens to wager (min 10 TP)
   * @param userId User identifier, defaults to "default_user"
   */
  fun placeBet(
    matchId: String,
    selection: String,
    odds: Double,
    stakeAmount: Long,
    userId: String = "default_user"
  ) {
    viewModelScope.launch {
      _betPlacementState.value = BetPlacementState.Loading
      val result = executePlaceBet(matchId, selection, odds, stakeAmount, userId)
      if (result.isSuccess) {
        val entry = result.getOrThrow()
        val currentBal = virtualTokenBalance.value
        _betPlacementState.value = BetPlacementState.Success(
          betId = entry.betId,
          newBalance = currentBal,
          potentialReturn = entry.potentialReturn,
          message = "✅ Kupon onaylandı! -$stakeAmount TP düşüldü. (Potansiyel: ${entry.potentialReturn} TP)"
        )
      } else {
        val errorMsg = result.exceptionOrNull()?.message ?: "Bilinmeyen bahis hatası"
        _betPlacementState.value = BetPlacementState.Error(errorMsg)
      }
    }
  }

  /**
   * Internal suspend function that strictly validates balance and active match status
   * before performing atomic database updates.
   */
  suspend fun executePlaceBet(
    matchId: String,
    selection: String,
    odds: Double,
    stakeAmount: Long,
    userId: String = "default_user"
  ): Result<BetEntry> = withContext(Dispatchers.IO) {
    // Validation 1: Stake amount validity
    if (stakeAmount < 10L) {
      return@withContext Result.failure(
        IllegalArgumentException("⚠️ Geçersiz bahis tutarı: Minimum kupon bedeli 10 TP olmalıdır.")
      )
    }

    if (odds <= 1.0) {
      return@withContext Result.failure(
        IllegalArgumentException("⚠️ Geçersiz oran: Kupon oranı 1.00'den büyük olmalıdır.")
      )
    }

    // Validation 2: Check user balance
    val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
    if (currentWallet.availablePoints < stakeAmount) {
      return@withContext Result.failure(
        IllegalStateException(
          "⚠️ Yetersiz TP bakiyesi! Mevcut bakiyeniz: ${currentWallet.availablePoints} TP, Kupon bedeli: $stakeAmount TP."
        )
      )
    }

    // Validation 3: Check if match is still active in Room database
    val match = simulatedMatchDao.getMatchById(matchId)
      ?: return@withContext Result.failure(
        IllegalArgumentException("⚠️ Karşılaşma bulunamadı: $matchId")
      )

    if (match.status == "FINISHED") {
      return@withContext Result.failure(
        IllegalStateException(
          "⚠️ Karşılaşma sona erdi! (${match.homeTeam} - ${match.awayTeam}). Sona eren maçlara bahis yapılamaz."
        )
      )
    }

    val activeStatuses = listOf("LIVE", "UPCOMING", "SCHEDULED")
    if (match.status !in activeStatuses) {
      return@withContext Result.failure(
        IllegalStateException(
          "⚠️ Karşılaşma aktif değil (${match.status}): ${match.homeTeam} - ${match.awayTeam}. Bahis askıya alındı."
        )
      )
    }

    // Calculations
    val potentialReturn = (stakeAmount * odds).toLong()
    val betId = "BET-${UUID.randomUUID().toString().take(8).uppercase()}"
    val ticketNumber = "TK-${(1000000..9999999).random()}"
    val now = System.currentTimeMillis()

    val selectionsArray = JSONArray().apply {
      put(
        JSONObject().apply {
          put("matchId", match.id)
          put("homeTeam", match.homeTeam)
          put("awayTeam", match.awayTeam)
          put("matchTeams", "${match.homeTeam} - ${match.awayTeam}")
          put("selection", selection)
          put("odds", odds)
          put("status", match.status)
          put("sport", match.sport)
        }
      )
    }

    val matchSummary = "${match.homeTeam} - ${match.awayTeam} ($selection @$odds)"

    // 1. Deduct tokens from wallet
    val newBalance = currentWallet.availablePoints - stakeAmount
    walletDao.insertOrUpdateWallet(
      currentWallet.copy(
        availablePoints = newBalance,
        lifetimeLost = currentWallet.lifetimeLost + stakeAmount
      )
    )

    // 2. Insert transaction history
    walletDao.insertTransaction(
      TransactionEntity(
        type = "BET_STAKE",
        amount = -stakeAmount,
        balanceAfter = newBalance,
        description = "Kupon Oynandı ($betId): $matchSummary",
        createdAt = now
      )
    )

    // 3. Sync UserBalance entity
    userBalanceDao.updateBalance(userId, newBalance, now)

    // 4. Insert BetEntry into Room
    val betEntry = BetEntry(
      betId = betId,
      userId = userId,
      stakeAmount = stakeAmount,
      totalOdds = odds,
      potentialReturn = potentialReturn,
      status = "PENDING",
      placedAt = now,
      resolvedAt = null,
      matchSummary = matchSummary,
      selectionsJson = selectionsArray.toString()
    )
    val insertedId = betEntryDao.insertBetEntry(betEntry)

    // 5. Insert TicketEntity for UI integration
    val ticket = TicketEntity(
      ticketNumber = ticketNumber,
      type = "TEKLI",
      stakePoints = stakeAmount,
      totalOdds = odds,
      potentialPoints = potentialReturn,
      status = "PENDING",
      createdAt = now,
      selectionsJson = selectionsArray.toString()
    )
    ticketDao.insertTicket(ticket)

    return@withContext Result.success(betEntry.copy(id = insertedId))
  }

  /**
   * Places a multi-match accumulator coupon with strict active match validation for each selection.
   */
  fun placeMultiMatchBet(
    matchIds: List<String>,
    selectionNames: List<String>,
    oddsList: List<Double>,
    stakeAmount: Long,
    userId: String = "default_user"
  ) {
    viewModelScope.launch {
      _betPlacementState.value = BetPlacementState.Loading
      val result = withContext(Dispatchers.IO) {
        if (matchIds.isEmpty() || matchIds.size != selectionNames.size || matchIds.size != oddsList.size) {
          return@withContext Result.failure<BetEntry>(
            IllegalArgumentException("⚠️ Kupon seçimleri eksik veya hatalı.")
          )
        }

        if (stakeAmount < 10L) {
          return@withContext Result.failure(
            IllegalArgumentException("⚠️ Minimum kupon bedeli 10 TP'dir.")
          )
        }

        val wallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
        if (wallet.availablePoints < stakeAmount) {
          return@withContext Result.failure(
            IllegalStateException("⚠️ Yetersiz bakiye! Mevcut: ${wallet.availablePoints} TP, Kupon: $stakeAmount TP")
          )
        }

        // Validate each match is still active in Room
        val validatedMatches = mutableListOf<Match>()
        for (mId in matchIds) {
          val match = simulatedMatchDao.getMatchById(mId)
            ?: return@withContext Result.failure(
              IllegalArgumentException("⚠️ Karşılaşma veritabanında bulunamadı: ID $mId")
            )

          if (match.status == "FINISHED") {
            return@withContext Result.failure(
              IllegalStateException(
                "⚠️ Kupondaki maç sona ermiş: ${match.homeTeam} - ${match.awayTeam}. Kupon oluşturulamaz."
              )
            )
          }

          if (match.status !in listOf("LIVE", "UPCOMING", "SCHEDULED")) {
            return@withContext Result.failure(
              IllegalStateException(
                "⚠️ Karşılaşma aktif değil (${match.status}): ${match.homeTeam} - ${match.awayTeam}."
              )
            )
          }
          validatedMatches.add(match)
        }

        // Calculate combined odds
        val rawOdds = oddsList.fold(1.0) { acc, o -> acc * o }
        val totalOdds = (rawOdds * 100).toLong() / 100.0
        val potentialReturn = (stakeAmount * totalOdds).toLong()
        val betId = "BET-${UUID.randomUUID().toString().take(8).uppercase()}"
        val ticketNumber = "TK-${(1000000..9999999).random()}"
        val now = System.currentTimeMillis()

        val jsonArray = JSONArray()
        for (i in validatedMatches.indices) {
          val m = validatedMatches[i]
          jsonArray.put(
            JSONObject().apply {
              put("matchId", m.id)
              put("matchTeams", "${m.homeTeam} - ${m.awayTeam}")
              put("selection", selectionNames[i])
              put("odds", oddsList[i])
              put("status", m.status)
            }
          )
        }

        val summary = if (validatedMatches.size == 1) {
          "${validatedMatches[0].homeTeam} - ${validatedMatches[0].awayTeam} (${selectionNames[0]})"
        } else {
          "${validatedMatches[0].homeTeam} + ${validatedMatches.size - 1} maç"
        }

        val newBalance = wallet.availablePoints - stakeAmount
        walletDao.insertOrUpdateWallet(
          wallet.copy(
            availablePoints = newBalance,
            lifetimeLost = wallet.lifetimeLost + stakeAmount
          )
        )

        walletDao.insertTransaction(
          TransactionEntity(
            type = "BET_STAKE",
            amount = -stakeAmount,
            balanceAfter = newBalance,
            description = "Kombine Kupon ($betId): $summary (Oran: $totalOdds)",
            createdAt = now
          )
        )

        val betEntry = BetEntry(
          betId = betId,
          userId = userId,
          stakeAmount = stakeAmount,
          totalOdds = totalOdds,
          potentialReturn = potentialReturn,
          status = "PENDING",
          placedAt = now,
          resolvedAt = null,
          matchSummary = summary,
          selectionsJson = jsonArray.toString()
        )
        val id = betEntryDao.insertBetEntry(betEntry)

        ticketDao.insertTicket(
          TicketEntity(
            ticketNumber = ticketNumber,
            type = if (validatedMatches.size > 1) "KOMBINE" else "TEKLI",
            stakePoints = stakeAmount,
            totalOdds = totalOdds,
            potentialPoints = potentialReturn,
            status = "PENDING",
            createdAt = now,
            selectionsJson = jsonArray.toString()
          )
        )

        Result.success(betEntry.copy(id = id))
      }

      if (result.isSuccess) {
        val entry = result.getOrThrow()
        _betPlacementState.value = BetPlacementState.Success(
          betId = entry.betId,
          newBalance = virtualTokenBalance.value,
          potentialReturn = entry.potentialReturn,
          message = "🎉 Kupon Onaylandı! -$stakeAmount TP düşüldü. (Potansiyel: ${entry.potentialReturn} TP)"
        )
      } else {
        _betPlacementState.value = BetPlacementState.Error(
          result.exceptionOrNull()?.message ?: "Kupon oluşturulamadı."
        )
      }
    }
  }

  fun resetPlacementState() {
    _betPlacementState.value = BetPlacementState.Idle
  }

  /**
   * Invokes Gemini AI to analyze match history and current form from the Room database,
   * calculating a win-probability percentage, expected score, and tactical factors.
   */
  fun requestGeminiWinProbabilityAnalysis(matchId: String) {
    viewModelScope.launch {
      _isAnalyzingProbability.value = true
      try {
        val analysis = geminiProbabilityService.analyzeMatchById(matchId)
        _winProbabilityAnalysis.value = analysis
      } catch (_: Exception) {
        // Handled internally by service
      } finally {
        _isAnalyzingProbability.value = false
      }
    }
  }

  fun clearWinProbabilityAnalysis() {
    _winProbabilityAnalysis.value = null
  }

  /**
   * Helper to seed realistic matches across multiple sports into the Room database,
   * including specialized Basketball (periods/rebounds), Tennis (sets/games), and F1 (pit-stops/lap times).
   */
  private suspend fun seedSampleSimulatedMatches() {
    val sampleMatches = listOf(
      Match(
        id = "sim_match_1",
        homeTeam = "Galatasaray",
        awayTeam = "Fenerbahçe",
        homeScore = 2,
        awayScore = 1,
        status = "LIVE",
        homeOdds = 2.10,
        drawOdds = 3.20,
        awayOdds = 2.90,
        over25Odds = 1.70,
        under25Odds = 1.95,
        sport = "FOOTBALL",
        league = "Trendyol Süper Lig",
        minute = 67,
        startTime = "20:00",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_2",
        homeTeam = "Real Madrid",
        awayTeam = "Barcelona",
        homeScore = 0,
        awayScore = 0,
        status = "UPCOMING",
        homeOdds = 1.95,
        drawOdds = 3.60,
        awayOdds = 3.10,
        over25Odds = 1.60,
        under25Odds = 2.10,
        sport = "FOOTBALL",
        league = "La Liga",
        minute = 0,
        startTime = "22:00",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_3",
        homeTeam = "Boston Celtics",
        awayTeam = "Los Angeles Lakers",
        homeScore = 84,
        awayScore = 82,
        status = "LIVE",
        homeOdds = 1.65,
        drawOdds = 14.0,
        awayOdds = 2.25,
        over25Odds = 1.88,
        under25Odds = 1.88,
        sport = "BASKETBALL",
        league = "NBA",
        minute = 32,
        startTime = "03:30",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_4",
        homeTeam = "Carlos Alcaraz",
        awayTeam = "Jannik Sinner",
        homeScore = 1,
        awayScore = 1,
        status = "LIVE",
        homeOdds = 1.80,
        drawOdds = 1.0,
        awayOdds = 1.95,
        over25Odds = 1.85,
        under25Odds = 1.85,
        sport = "TENNIS",
        league = "ATP Finals",
        minute = 95,
        startTime = "18:00",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_5",
        homeTeam = "VakıfBank",
        awayTeam = "Eczacıbaşı Dynavit",
        homeScore = 2,
        awayScore = 0,
        status = "LIVE",
        homeOdds = 1.55,
        drawOdds = 1.0,
        awayOdds = 2.45,
        over25Odds = 1.80,
        under25Odds = 1.90,
        sport = "VOLLEYBALL",
        league = "Sultanlar Ligi",
        minute = 45,
        startTime = "17:30",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_6",
        homeTeam = "Islam Makhachev",
        awayTeam = "Arman Tsarukyan",
        homeScore = 0,
        awayScore = 0,
        status = "UPCOMING",
        homeOdds = 1.40,
        drawOdds = 30.0,
        awayOdds = 2.90,
        over25Odds = 1.75,
        under25Odds = 1.95,
        sport = "MMA_UFC",
        league = "UFC Lightweight Title",
        minute = 0,
        startTime = "23:45",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_7",
        homeTeam = "Kansas City Chiefs",
        awayTeam = "San Francisco 49ers",
        homeScore = 14,
        awayScore = 10,
        status = "LIVE",
        homeOdds = 1.75,
        drawOdds = 12.0,
        awayOdds = 2.10,
        over25Odds = 1.90,
        under25Odds = 1.85,
        sport = "AMERICAN_FOOTBALL",
        league = "NFL Week 4",
        minute = 28,
        startTime = "21:15",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_8",
        homeTeam = "New York Yankees",
        awayTeam = "LA Dodgers",
        homeScore = 0,
        awayScore = 0,
        status = "UPCOMING",
        homeOdds = 1.90,
        drawOdds = 1.0,
        awayOdds = 1.85,
        over25Odds = 1.85,
        under25Odds = 1.85,
        sport = "BASEBALL",
        league = "MLB World Series",
        minute = 0,
        startTime = "02:05",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_9",
        homeTeam = "Ma Long",
        awayTeam = "Fan Zhendong",
        homeScore = 2,
        awayScore = 2,
        status = "LIVE",
        homeOdds = 1.92,
        drawOdds = 1.0,
        awayOdds = 1.82,
        over25Odds = 1.85,
        under25Odds = 1.85,
        sport = "TABLE_TENNIS",
        league = "WTT Champions",
        minute = 35,
        startTime = "15:00",
        matchDateIso = "2026-09-30"
      ),
      Match(
        id = "sim_match_10",
        homeTeam = "Manchester City",
        awayTeam = "Arsenal",
        homeScore = 2,
        awayScore = 2,
        status = "FINISHED", // Sona ermiş maç örneği
        homeOdds = 1.85,
        drawOdds = 3.50,
        awayOdds = 3.80,
        over25Odds = 1.65,
        under25Odds = 2.15,
        sport = "FOOTBALL",
        league = "Premier League",
        minute = 90,
        startTime = "18:30",
        matchDateIso = "2026-09-30"
      )
    )
    simulatedMatchDao.insertMatches(sampleMatches)

    // Seed specialized Basketball matches (periods & rebounds)
    val sampleBasketball = listOf(
      com.example.data.local.BasketballMatchEntity(
        id = "bb_spec_1",
        league = "Türkiye Sigorta Basketbol Süper Ligi",
        homeTeam = "Fenerbahçe Beko",
        awayTeam = "Anadolu Efes",
        status = "LIVE",
        totalHomeScore = 78,
        totalAwayScore = 74,
        quarter = 4,
        timeRemaining = "03:45",
        q1Home = 22, q1Away = 19,
        q2Home = 18, q2Away = 21,
        q3Home = 24, q3Away = 20,
        q4Home = 14, q4Away = 14,
        offensiveReboundsHome = 11, offensiveReboundsAway = 8,
        defensiveReboundsHome = 26, defensiveReboundsAway = 24,
        totalReboundsHome = 37, totalReboundsAway = 32,
        assistsHome = 21, assistsAway = 18,
        teamFoulsHome = 4, teamFoulsAway = 5,
        oddsHomeWin = 1.62, oddsAwayWin = 2.25,
        handicapHome = -3.5, handicapOdds = 1.88,
        totalOverUnderThreshold = 162.5, overOdds = 1.85, underOdds = 1.85,
        startTime = "19:00", matchDateIso = "2026-09-30"
      ),
      com.example.data.local.BasketballMatchEntity(
        id = "bb_spec_2",
        league = "EuroLeague",
        homeTeam = "Real Madrid Baloncesto",
        awayTeam = "Panathinaikos",
        status = "UPCOMING",
        totalHomeScore = 0, totalAwayScore = 0,
        quarter = 1, timeRemaining = "10:00",
        q1Home = 0, q1Away = 0, q2Home = 0, q2Away = 0,
        q3Home = 0, q3Away = 0, q4Home = 0, q4Away = 0,
        offensiveReboundsHome = 0, offensiveReboundsAway = 0,
        defensiveReboundsHome = 0, defensiveReboundsAway = 0,
        totalReboundsHome = 0, totalReboundsAway = 0,
        assistsHome = 0, assistsAway = 0,
        teamFoulsHome = 0, teamFoulsAway = 0,
        oddsHomeWin = 1.70, oddsAwayWin = 2.10,
        handicapHome = -2.5, handicapOdds = 1.90,
        totalOverUnderThreshold = 165.5, overOdds = 1.85, underOdds = 1.85,
        startTime = "21:45", matchDateIso = "2026-09-30"
      )
    )
    basketballMatchDao.insertMatches(sampleBasketball)

    // Seed specialized Tennis matches (sets, games, tie-breaks, aces)
    val sampleTennis = listOf(
      com.example.data.local.TennisMatchEntity(
        id = "ten_spec_1",
        tournament = "ATP Finals Torino - Yarı Final",
        courtSurface = "Hard",
        round = "Yarı Final",
        player1 = "Carlos Alcaraz",
        player2 = "Jannik Sinner",
        status = "LIVE",
        setsWonPlayer1 = 1,
        setsWonPlayer2 = 1,
        currentSet = 3,
        servingPlayer = "player1",
        set1Player1 = 6, set1Player2 = 4,
        set2Player1 = 4, set2Player2 = 6,
        set3Player1 = 4, set3Player2 = 3,
        currentGameScore1 = "40", currentGameScore2 = "30",
        tieBreakSet1 = "", tieBreakSet2 = "",
        acesPlayer1 = 9, acesPlayer2 = 11,
        doubleFaultsPlayer1 = 2, doubleFaultsPlayer2 = 1,
        breakPointsWonPlayer1 = 2, breakPointsWonPlayer2 = 2,
        firstServePercentagePlayer1 = 68, firstServePercentagePlayer2 = 71,
        oddsPlayer1 = 1.80, oddsPlayer2 = 1.95,
        totalGamesThreshold = 24.5, overGamesOdds = 1.80, underGamesOdds = 1.95,
        startTime = "18:00", matchDateIso = "2026-09-30"
      ),
      com.example.data.local.TennisMatchEntity(
        id = "ten_spec_2",
        tournament = "Wimbledon - Çeyrek Final",
        courtSurface = "Grass",
        round = "Çeyrek Final",
        player1 = "Novak Djokovic",
        player2 = "Daniil Medvedev",
        status = "UPCOMING",
        setsWonPlayer1 = 0, setsWonPlayer2 = 0,
        currentSet = 1, servingPlayer = "player1",
        set1Player1 = 0, set1Player2 = 0,
        set2Player1 = 0, set2Player2 = 0,
        set3Player1 = 0, set3Player2 = 0,
        currentGameScore1 = "0", currentGameScore2 = "0",
        tieBreakSet1 = "", tieBreakSet2 = "",
        acesPlayer1 = 0, acesPlayer2 = 0,
        doubleFaultsPlayer1 = 0, doubleFaultsPlayer2 = 0,
        breakPointsWonPlayer1 = 0, breakPointsWonPlayer2 = 0,
        firstServePercentagePlayer1 = 70, firstServePercentagePlayer2 = 65,
        oddsPlayer1 = 1.55, oddsPlayer2 = 2.45,
        totalGamesThreshold = 22.5, overGamesOdds = 1.85, underGamesOdds = 1.85,
        startTime = "20:30", matchDateIso = "2026-09-30"
      )
    )
    tennisMatchDao.insertMatches(sampleTennis)

    // Seed specialized Formula 1 races (pit-stops, lap times, tyre telemetry)
    val sampleF1 = listOf(
      com.example.data.local.Formula1RaceEntity(
        id = "f1_spec_1",
        grandPrixName = "Intercity İstanbul Park GP 2026",
        circuitName = "İstanbul Park 8. Viraj",
        country = "Türkiye 🇹🇷",
        status = "LIVE",
        raceDateIso = "2026-09-30",
        startTime = "15:00",
        currentLap = 42,
        totalLaps = 58,
        leaderDriver = "Max Verstappen (Red Bull)",
        secondDriver = "Lando Norris (McLaren)",
        thirdDriver = "Charles Leclerc (Ferrari)",
        gapToLeaderSeconds = "+1.642s",
        fastestLapDriver = "Lando Norris",
        fastestLapTime = "1:22.115",
        sector1Time = "28.1s",
        sector2Time = "33.9s",
        sector3Time = "22.4s",
        leaderPitStops = 1,
        secondPitStops = 2,
        lastPitStopLap = 31,
        leaderTyreCompound = "HARD",
        tyreLapsAge = 11,
        trackStatus = "GREEN_FLAG",
        oddsLeaderWin = 1.58,
        oddsSecondWin = 2.45,
        oddsThirdWin = 5.20,
        oddsFastestLap = 2.15
      ),
      com.example.data.local.Formula1RaceEntity(
        id = "f1_spec_2",
        grandPrixName = "Monaco Grand Prix 2026",
        circuitName = "Circuit de Monaco",
        country = "Monaco 🇲🇨",
        status = "UPCOMING",
        raceDateIso = "2026-10-04",
        startTime = "16:00",
        currentLap = 0,
        totalLaps = 78,
        leaderDriver = "Charles Leclerc",
        secondDriver = "Max Verstappen",
        thirdDriver = "Lewis Hamilton",
        gapToLeaderSeconds = "0.0s",
        fastestLapDriver = "-",
        fastestLapTime = "-",
        sector1Time = "-",
        sector2Time = "-",
        sector3Time = "-",
        leaderPitStops = 0,
        secondPitStops = 0,
        lastPitStopLap = 0,
        leaderTyreCompound = "SOFT",
        tyreLapsAge = 0,
        trackStatus = "GREEN_FLAG",
        oddsLeaderWin = 2.10,
        oddsSecondWin = 2.20,
        oddsThirdWin = 4.80,
        oddsFastestLap = 2.30
      )
    )
    formula1RaceDao.insertRaces(sampleF1)
  }
}
