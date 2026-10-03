package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BetEntry
import com.example.data.local.TicketEntity
import com.example.data.local.UserBalanceManager
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.TicketType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * Validation result for bet placement requests.
 */
sealed class BetValidationResult {
  object Valid : BetValidationResult()
  data class Invalid(val reason: String) : BetValidationResult()
}

/**
 * Repository interface defining betting operations, validation, and settlement logic.
 */
interface BetRepository {
  val activeBetsFlow: Flow<List<BetEntry>>
  val allBetsFlow: Flow<List<BetEntry>>

  fun calculateTotalOdds(selections: List<SlipSelection>): Double
  fun calculatePotentialWinnings(stake: Long, selections: List<SlipSelection>): Long

  suspend fun validateBet(
    stake: Long,
    selections: List<SlipSelection>,
    userId: String = "default_user"
  ): BetValidationResult

  suspend fun placeBet(
    selections: List<SlipSelection>,
    stake: Long,
    ticketType: TicketType = TicketType.KOMBINE,
    userId: String = "default_user"
  ): Result<BetEntry>

  suspend fun settleBet(
    betId: String,
    status: String,
    actualWinnings: Long = 0L,
    userId: String = "default_user"
  ): Result<Unit>

  suspend fun evaluatePendingBets(currentMatches: List<Match>): Int
}

/**
 * BetRepository implementation handling the business logic of placing bets on simulated match events,
 * validating available virtual currency (TP) through [UserBalanceManager], and computing winnings.
 */
class BetRepositoryImpl(
  private val database: AppDatabase,
  private val balanceManager: UserBalanceManager
) : BetRepository {

  private val betEntryDao = database.betEntryDao()
  private val ticketDao = database.ticketDao()
  private val simulatedMatchDao = database.simulatedMatchDao()
  private val matchDao = database.matchDao()

  override val activeBetsFlow: Flow<List<BetEntry>> = betEntryDao.getBetEntriesByStatusFlow("PENDING")
  override val allBetsFlow: Flow<List<BetEntry>> = betEntryDao.getBetEntriesFlow("default_user")

  /**
   * Calculates compound total odds for a set of selections, rounded to 2 decimal places.
   */
  override fun calculateTotalOdds(selections: List<SlipSelection>): Double {
    if (selections.isEmpty()) return 1.00
    val rawOdds = selections.fold(1.0) { acc, sel -> acc * sel.odd }
    return (rawOdds * 100).roundToLong() / 100.0
  }

  /**
   * Computes potential winnings in TP given a stake and a list of slip selections.
   */
  override fun calculatePotentialWinnings(stake: Long, selections: List<SlipSelection>): Long {
    if (stake <= 0 || selections.isEmpty()) return 0L
    val totalOdds = calculateTotalOdds(selections)
    return (stake * totalOdds).roundToLong()
  }

  /**
   * Performs rigorous validation:
   * 1. Minimum 1 selection required.
   * 2. Stake must be at least 10 TP.
   * 3. Available balance must be sufficient.
   * 4. Selections must not contain finished/locked matches.
   */
  override suspend fun validateBet(
    stake: Long,
    selections: List<SlipSelection>,
    userId: String
  ): BetValidationResult = withContext(Dispatchers.IO) {
    if (selections.isEmpty()) {
      return@withContext BetValidationResult.Invalid("Kuponunuzda en az 1 karşılaşma bulunmalıdır.")
    }
    if (stake < 10) {
      return@withContext BetValidationResult.Invalid("Minimum kupon bedeli 10 TP'dir.")
    }

    val available = balanceManager.getAvailablePoints(userId)
    if (available < stake) {
      return@withContext BetValidationResult.Invalid(
        "Yetersiz bakiye! Mevcut bakiyeniz: $available TP, Kupon bedeli: $stake TP."
      )
    }

    // Check that each selection's match is still active in Room database
    for (sel in selections) {
      val simMatch = simulatedMatchDao.getMatchById(sel.matchId)
      if (simMatch != null) {
        if (simMatch.status == "FINISHED") {
          return@withContext BetValidationResult.Invalid(
            "Karşılaşma sona erdi: ${simMatch.homeTeam} - ${simMatch.awayTeam}. Sona eren maçlara bahis oynanamaz."
          )
        }
        if (simMatch.status !in listOf("LIVE", "UPCOMING", "SCHEDULED")) {
          return@withContext BetValidationResult.Invalid(
            "Karşılaşma askıya alındı veya aktif değil: ${simMatch.homeTeam} - ${simMatch.awayTeam}."
          )
        }
      } else {
        val entityMatch = matchDao.getMatchById(sel.matchId)
        if (entityMatch != null) {
          if (entityMatch.status == "FINISHED") {
            return@withContext BetValidationResult.Invalid(
              "Karşılaşma sona erdi: ${entityMatch.homeTeam} - ${entityMatch.awayTeam}."
            )
          }
        }
      }
    }

    return@withContext BetValidationResult.Valid
  }

  /**
   * Places a bet on simulated match events:
   * - Validates stake and selections.
   * - Deducts stake from user balance.
   * - Calculates potential winnings.
   * - Persists BetEntry and TicketEntity in Room.
   */
  override suspend fun placeBet(
    selections: List<SlipSelection>,
    stake: Long,
    ticketType: TicketType,
    userId: String
  ): Result<BetEntry> = withContext(Dispatchers.IO) {
    val validation = validateBet(stake, selections, userId)
    if (validation is BetValidationResult.Invalid) {
      return@withContext Result.failure(IllegalArgumentException(validation.reason))
    }

    val totalOdds = calculateTotalOdds(selections)
    val potentialWinnings = calculatePotentialWinnings(stake, selections)
    val betId = "BET-${Random.nextInt(100000, 999999)}"
    val ticketNumber = "TK-${Random.nextInt(1000000, 9999999)}"

    // 1. Deduct virtual TP currency via UserBalanceManager
    val deductionSuccess = balanceManager.deductStake(
      amount = stake,
      description = "Kupon Oynandı ($betId - ${selections.size} Maç, $totalOdds Oran)",
      userId = userId
    )

    if (!deductionSuccess) {
      return@withContext Result.failure(IllegalStateException("Bakiye düşümü gerçekleştirilemedi."))
    }

    // 2. Serialize selections into JSON
    val selectionsJsonArray = JSONArray()
    for (sel in selections) {
      val obj = JSONObject().apply {
        put("matchId", sel.matchId)
        put("matchTeams", sel.matchTeams)
        put("marketType", sel.marketType.name)
        put("selectionId", sel.selectionId)
        put("selectionName", sel.selectionName)
        put("odd", sel.odd)
        put("isLive", sel.isLive)
      }
      selectionsJsonArray.put(obj)
    }
    val jsonString = selectionsJsonArray.toString()

    val matchSummary = if (selections.size == 1) {
      "${selections[0].matchTeams} (${selections[0].selectionName})"
    } else {
      "${selections[0].matchTeams} + ${selections.size - 1} maç"
    }

    val now = System.currentTimeMillis()

    // 3. Persist BetEntry
    val betEntry = BetEntry(
      betId = betId,
      userId = userId,
      stakeAmount = stake,
      totalOdds = totalOdds,
      potentialReturn = potentialWinnings,
      status = "PENDING",
      placedAt = now,
      resolvedAt = null,
      matchSummary = matchSummary,
      selectionsJson = jsonString
    )
    val insertedId = betEntryDao.insertBetEntry(betEntry)

    // 4. Also persist TicketEntity for legacy UI compatibility
    val ticketEntity = TicketEntity(
      ticketNumber = ticketNumber,
      type = ticketType.name,
      stakePoints = stake,
      totalOdds = totalOdds,
      potentialPoints = potentialWinnings,
      status = "PENDING",
      createdAt = now,
      selectionsJson = jsonString
    )
    ticketDao.insertTicket(ticketEntity)

    return@withContext Result.success(betEntry.copy(id = insertedId))
  }

  /**
   * Settles a specific bet (e.g. WON, LOST, CANCELLED).
   * Automatically credits user winnings to [UserBalanceManager] if won.
   */
  override suspend fun settleBet(
    betId: String,
    status: String,
    actualWinnings: Long,
    userId: String
  ): Result<Unit> = withContext(Dispatchers.IO) {
    val existing = betEntryDao.getBetEntryById(betId)
      ?: return@withContext Result.failure(IllegalArgumentException("Kupon bulunamadı: $betId"))

    if (existing.status != "PENDING") {
      return@withContext Result.failure(IllegalStateException("Kupon zaten sonuçlandırılmış: ${existing.status}"))
    }

    val now = System.currentTimeMillis()
    val updated = existing.copy(
      status = status,
      resolvedAt = now
    )
    betEntryDao.updateBetEntry(updated)

    if (status == "WON" && actualWinnings > 0) {
      balanceManager.addWinnings(
        amount = actualWinnings,
        description = "Kupon Kazancı ($betId) 🎉",
        userId = userId
      )
    }
    balanceManager.recordBetResultInStats(
      userId = userId,
      isWon = (status == "WON"),
      tpWon = if (status == "WON") actualWinnings else 0L
    )

    return@withContext Result.success(Unit)
  }

  /**
   * Evaluates pending bets against ongoing or finished matches from the simulation engine.
   * Returns the number of bets settled during this pass.
   */
  override suspend fun evaluatePendingBets(currentMatches: List<Match>): Int = withContext(Dispatchers.IO) {
    val pendingTickets = ticketDao.getPendingTickets()
    if (pendingTickets.isEmpty()) return@withContext 0

    var settledCount = 0

    for (ticketEntity in pendingTickets) {
      val selections = parseSelectionsJson(ticketEntity.selectionsJson)
      var allMatchesFinished = true
      var hasLost = false

      for (sel in selections) {
        val match = currentMatches.find { it.id == sel.matchId }
        if (match == null || match.status != MatchStatus.FINISHED) {
          allMatchesFinished = false
          break
        }

        // Check if selection won
        val won = when (sel.marketType) {
          MarketType.MATCH_RESULT -> {
            when {
              sel.selectionName.contains("1") -> match.homeScore > match.awayScore
              sel.selectionName.contains("2") -> match.awayScore > match.homeScore
              else -> match.homeScore == match.awayScore
            }
          }
          MarketType.TOTAL_GOALS_25 -> {
            val total = match.homeScore + match.awayScore
            if (sel.selectionName.contains("Üst", ignoreCase = true)) total > 2 else total < 3
          }
          MarketType.BOTH_TEAMS_SCORE -> {
            val bothScored = match.homeScore > 0 && match.awayScore > 0
            if (sel.selectionName.contains("Var", ignoreCase = true)) bothScored else !bothScored
          }
          MarketType.DOUBLE_CHANCE -> {
            when {
              sel.selectionName.contains("1-X") -> match.homeScore >= match.awayScore
              sel.selectionName.contains("1-2") -> match.homeScore != match.awayScore
              else -> match.awayScore >= match.homeScore
            }
          }
          else -> true
        }

        if (!won) {
          hasLost = true
          break
        }
      }

      if (hasLost) {
        ticketDao.updateTicket(ticketEntity.copy(status = "LOST"))
        balanceManager.recordBetResultInStats("default_user", isWon = false, tpWon = 0L)
        settledCount++
      } else if (allMatchesFinished && selections.isNotEmpty()) {
        val winPoints = ticketEntity.potentialPoints
        ticketDao.updateTicket(ticketEntity.copy(status = "WON"))
        balanceManager.addWinnings(
          amount = winPoints,
          description = "Kupon Kazancı (${ticketEntity.ticketNumber}) 🎉"
        )
        balanceManager.recordBetResultInStats("default_user", isWon = true, tpWon = winPoints)
        settledCount++
      }
    }

    return@withContext settledCount
  }

  private fun parseSelectionsJson(json: String): List<SlipSelection> {
    if (json.isBlank()) return emptyList()
    return try {
      val array = JSONArray(json)
      val list = mutableListOf<SlipSelection>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          SlipSelection(
            matchId = obj.getString("matchId"),
            matchTeams = obj.getString("matchTeams"),
            marketType = MarketType.valueOf(obj.getString("marketType")),
            selectionId = obj.getString("selectionId"),
            selectionName = obj.getString("selectionName"),
            odd = obj.getDouble("odd"),
            isLive = obj.optBoolean("isLive", false)
          )
        )
      }
      list
    } catch (_: Exception) {
      emptyList()
    }
  }
}
