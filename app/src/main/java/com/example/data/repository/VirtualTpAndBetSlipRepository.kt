package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BetEntry
import com.example.data.local.TicketEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserBalance
import com.example.data.local.WalletEntity
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.data.model.TicketType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * Clean domain data model for user's virtual TP balance.
 */
data class VirtualBalanceModel(
  val userId: String = "default_user",
  val balance: Long = 10000L,
  val currencyCode: String = "TP",
  val lockedBalance: Long = 0L,
  val totalWon: Long = 0L,
  val totalLost: Long = 0L,
  val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Clean domain data model for simulated bet slips stored in Room.
 */
data class SimulatedBetSlip(
  val id: Long = 0,
  val ticketNumber: String,
  val type: String, // TEKLI, KOMBINE, SISTEM
  val stakePoints: Long,
  val totalOdds: Double,
  val potentialPoints: Long,
  val status: String, // PENDING, WON, LOST, CASHED_OUT
  val createdAt: Long,
  val selectionsJson: String,
  val selections: List<SlipSelection> = emptyList()
)

/**
 * Clean domain data model for TP balance ledger transactions.
 */
data class TpTransaction(
  val id: Long,
  val type: String,
  val amount: Long,
  val balanceAfter: Long,
  val description: String,
  val createdAt: Long
)

/**
 * Repository interface for storing virtual balance (TP) and simulated bet slips using Room Database.
 */
interface VirtualTpAndBetSlipRepository {
  val balanceFlow: Flow<Long>
  val virtualBalanceFlow: Flow<VirtualBalanceModel>
  val activeBetSlipsFlow: Flow<List<SimulatedBetSlip>>
  val allBetSlipsFlow: Flow<List<SimulatedBetSlip>>
  val transactionsFlow: Flow<List<TpTransaction>>

  suspend fun getAvailableBalance(userId: String = "default_user"): Long
  suspend fun placeSimulatedBetSlip(
    stake: Long,
    selections: List<SlipSelection>,
    ticketType: TicketType = TicketType.KOMBINE,
    userId: String = "default_user"
  ): Result<SimulatedBetSlip>

  fun calculateCashoutValue(slip: SimulatedBetSlip, currentMatches: List<Match>): Long
  suspend fun cashOutBetSlip(
    slip: SimulatedBetSlip,
    currentMatches: List<Match>,
    userId: String = "default_user"
  ): Result<Long>

  suspend fun evaluateAndSettleSlips(
    currentMatches: List<Match>,
    userId: String = "default_user"
  ): Int

  suspend fun addBonusTp(
    amount: Long,
    description: String,
    userId: String = "default_user"
  ): Long
}

/**
 * Implementation of [VirtualTpAndBetSlipRepository] backed by Room Database tables.
 */
class VirtualTpAndBetSlipRepositoryImpl(
  private val database: AppDatabase
) : VirtualTpAndBetSlipRepository {

  private val userBalanceDao = database.userBalanceDao()
  private val walletDao = database.walletDao()
  private val ticketDao = database.ticketDao()
  private val betEntryDao = database.betEntryDao()

  override val balanceFlow: Flow<Long> = userBalanceDao.getUserBalanceFlow("default_user")
    .map { it?.balance ?: 10000L }
    .distinctUntilChanged()
    .flowOn(Dispatchers.IO)

  override val virtualBalanceFlow: Flow<VirtualBalanceModel> = userBalanceDao.getUserBalanceFlow("default_user")
    .map { entity ->
      if (entity != null) {
        VirtualBalanceModel(
          userId = entity.userId,
          balance = entity.balance,
          currencyCode = entity.currencyCode,
          lockedBalance = entity.lockedBalance,
          totalWon = entity.totalWon,
          totalLost = entity.totalLost,
          lastUpdated = entity.lastUpdated
        )
      } else {
        VirtualBalanceModel()
      }
    }
    .distinctUntilChanged()
    .flowOn(Dispatchers.IO)

  override val activeBetSlipsFlow: Flow<List<SimulatedBetSlip>> = ticketDao.getTicketsByStatusFlow("PENDING")
    .map { list -> list.map { mapEntityToSimulatedBetSlip(it) } }
    .flowOn(Dispatchers.IO)

  override val allBetSlipsFlow: Flow<List<SimulatedBetSlip>> = ticketDao.getAllTicketsFlow()
    .map { list -> list.map { mapEntityToSimulatedBetSlip(it) } }
    .flowOn(Dispatchers.IO)

  override val transactionsFlow: Flow<List<TpTransaction>> = walletDao.getRecentTransactionsFlow()
    .map { list ->
      list.map {
        TpTransaction(
          id = it.id,
          type = it.type,
          amount = it.amount,
          balanceAfter = it.balanceAfter,
          description = it.description,
          createdAt = it.createdAt
        )
      }
    }
    .flowOn(Dispatchers.IO)

  override suspend fun getAvailableBalance(userId: String): Long = withContext(Dispatchers.IO) {
    val balance = userBalanceDao.getUserBalance(userId)?.balance
    if (balance != null) return@withContext balance

    // Initialize default balance if not present
    val defaultBal = 10000L
    userBalanceDao.insertOrUpdateBalance(
      UserBalance(
        userId = userId,
        balance = defaultBal,
        currencyCode = "TP"
      )
    )
    walletDao.insertOrUpdateWallet(
      WalletEntity(
        id = 1,
        availablePoints = defaultBal
      )
    )
    defaultBal
  }

  override suspend fun placeSimulatedBetSlip(
    stake: Long,
    selections: List<SlipSelection>,
    ticketType: TicketType,
    userId: String
  ): Result<SimulatedBetSlip> = withContext(Dispatchers.IO) {
    if (selections.isEmpty()) {
      return@withContext Result.failure(IllegalArgumentException("Kuponunuzda en az 1 maç bulunmalıdır."))
    }
    if (stake < 10L) {
      return@withContext Result.failure(IllegalArgumentException("Minimum kupon bedeli 10 TP'dir."))
    }

    val currentBalance = getAvailableBalance(userId)
    if (currentBalance < stake) {
      return@withContext Result.failure(
        IllegalArgumentException("Yetersiz bakiye! Mevcut bakiyeniz: $currentBalance TP, Kupon bedeli: $stake TP.")
      )
    }

    // Compound total odds
    val rawOdds = selections.fold(1.0) { acc, sel -> acc * sel.odd }
    val totalOdds = (rawOdds * 100).roundToLong() / 100.0
    val potentialPoints = (stake * totalOdds).roundToLong()

    val newBalance = currentBalance - stake
    val now = System.currentTimeMillis()
    val ticketNumber = "TK-${Random.nextInt(1000000, 9999999)}"

    // 1. Update UserBalance in Room
    userBalanceDao.insertOrUpdateBalance(
      UserBalance(
        userId = userId,
        balance = newBalance,
        lastUpdated = now
      )
    )

    // 2. Update Wallet and Transaction in Room
    val existingWallet = walletDao.getWallet() ?: WalletEntity(id = 1)
    walletDao.insertOrUpdateWallet(
      existingWallet.copy(
        availablePoints = newBalance,
        lifetimeLost = existingWallet.lifetimeLost + stake
      )
    )
    walletDao.insertTransaction(
      TransactionEntity(
        type = "BET_STAKE",
        amount = -stake,
        balanceAfter = newBalance,
        description = "Kupon Oynandı (#$ticketNumber - ${selections.size} Maç, $totalOdds Oran)",
        createdAt = now
      )
    )

    // 3. Serialize selections to JSON
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
    val selectionsJsonStr = selectionsJsonArray.toString()

    // 4. Save TicketEntity to Room
    val ticketEntity = TicketEntity(
      ticketNumber = ticketNumber,
      type = ticketType.name,
      stakePoints = stake,
      totalOdds = totalOdds,
      potentialPoints = potentialPoints,
      status = "PENDING",
      createdAt = now,
      selectionsJson = selectionsJsonStr
    )
    val insertedId = ticketDao.insertTicket(ticketEntity)

    // 5. Also save BetEntry in Room for history
    betEntryDao.insertBetEntry(
      BetEntry(
        betId = ticketNumber,
        userId = userId,
        stakeAmount = stake,
        totalOdds = totalOdds,
        potentialReturn = potentialPoints,
        status = "PENDING",
        placedAt = now,
        selectionsJson = selectionsJsonStr,
        matchSummary = selections.firstOrNull()?.let { "${it.matchTeams} (${it.selectionName})" } ?: "Kupon"
      )
    )

    val slip = SimulatedBetSlip(
      id = insertedId,
      ticketNumber = ticketNumber,
      type = ticketType.name,
      stakePoints = stake,
      totalOdds = totalOdds,
      potentialPoints = potentialPoints,
      status = "PENDING",
      createdAt = now,
      selectionsJson = selectionsJsonStr,
      selections = selections
    )

    Result.success(slip)
  }

  /**
   * Calculates cash-out value for a simulated bet slip based on the user's rule:
   * 1. Before kickoff (pre-match): Cashout value is exactly 50% of the placed stake.
   * 2. Once in-play: Cashout value dynamically increases/decreases automatically based on live score and minute progress.
   */
  override fun calculateCashoutValue(slip: SimulatedBetSlip, currentMatches: List<Match>): Long {
    if (slip.status != "PENDING" || slip.selections.isEmpty()) return 0L

    var anyLive = false
    var anyLost = false
    var allScheduled = true
    var liveScoreRatioSum = 0.0

    for (sel in slip.selections) {
      val match = currentMatches.firstOrNull { it.id == sel.matchId }
        ?: currentMatches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && (it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE) }
        ?: currentMatches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && it.status != MatchStatus.FINISHED }
        ?: currentMatches.firstOrNull { it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }

      if (match == null || match.status == MatchStatus.SCHEDULED || match.status == MatchStatus.UPCOMING) {
        liveScoreRatioSum += 0.50
        continue
      }

      allScheduled = false

      if (match.status == MatchStatus.FINISHED) {
        val won = evaluateSelectionWon(sel, match)
        if (!won) {
          anyLost = true
          break
        } else {
          liveScoreRatioSum += 1.0
        }
        continue
      }

      // In-play match
      anyLive = true
      val isWinningLive = evaluateSelectionWon(sel, match)
      val isDrawing = match.homeScore == match.awayScore
      val maxMin = when (match.sport) {
        Sport.BASKETBALL -> 40.0
        Sport.MOTORSPORTS -> 25.0
        else -> 90.0
      }
      val minuteProgress = (match.minute.coerceIn(1, maxMin.toInt()) / maxMin).coerceIn(0.05, 1.0)

      val matchFactor = if (isWinningLive) {
        0.55 + (minuteProgress * 0.40)
      } else if (isDrawing && (sel.selectionName.contains("1") || sel.selectionName.contains("2"))) {
        (0.50 - (minuteProgress * 0.28)).coerceAtLeast(0.12)
      } else {
        (0.38 - (minuteProgress * 0.30)).coerceAtLeast(0.06)
      }
      liveScoreRatioSum += matchFactor
    }

    if (anyLost) return 0L

    // RULE 1: Before matches start: exactly 50% of the invested stake points
    if (allScheduled && !anyLive) {
      return (slip.stakePoints / 2L).coerceAtLeast(1L)
    }

    // RULE 2: Once matches begin: dynamically rises and falls based on live game state
    val avg = (liveScoreRatioSum / slip.selections.size.toDouble()).coerceIn(0.05, 1.0)
    val calculated = if (avg >= 0.50) {
      val prog = (avg - 0.50) / 0.50
      val half = slip.stakePoints / 2.0
      val maxP = slip.potentialPoints * 0.95
      (half + (maxP - half) * prog).toLong()
    } else {
      val prog = (0.50 - avg) / 0.50
      val half = slip.stakePoints / 2.0
      val minP = (slip.stakePoints * 0.10).coerceAtLeast(1.0)
      (half - (half - minP) * prog).toLong()
    }

    return calculated.coerceIn(
      (slip.stakePoints * 0.05).toLong().coerceAtLeast(1L),
      (slip.potentialPoints * 0.98).toLong()
    )
  }

  override suspend fun cashOutBetSlip(
    slip: SimulatedBetSlip,
    currentMatches: List<Match>,
    userId: String
  ): Result<Long> = withContext(Dispatchers.IO) {
    if (slip.status != "PENDING") {
      return@withContext Result.failure(IllegalStateException("Bu kupon zaten sonuçlandırılmıştır: ${slip.status}"))
    }

    val cashoutAmount = calculateCashoutValue(slip, currentMatches)
    if (cashoutAmount <= 0) {
      return@withContext Result.failure(IllegalStateException("Kupon için bozdurma teklifi mevcut değil."))
    }

    val currentBalance = getAvailableBalance(userId)
    val newBalance = currentBalance + cashoutAmount
    val now = System.currentTimeMillis()

    // 1. Credit TP to UserBalance in Room
    userBalanceDao.insertOrUpdateBalance(
      UserBalance(
        userId = userId,
        balance = newBalance,
        lastUpdated = now
      )
    )

    // 2. Update Wallet and record transaction
    val existingWallet = walletDao.getWallet() ?: WalletEntity(id = 1)
    walletDao.insertOrUpdateWallet(
      existingWallet.copy(
        availablePoints = newBalance,
        lifetimeWon = existingWallet.lifetimeWon + cashoutAmount
      )
    )
    walletDao.insertTransaction(
      TransactionEntity(
        type = "BET_CASHOUT",
        amount = cashoutAmount,
        balanceAfter = newBalance,
        description = "Kupon Bozduruldu (#${slip.ticketNumber}) +$cashoutAmount TP 💰",
        createdAt = now
      )
    )

    // 3. Mark ticket as CASHED_OUT in Room
    ticketDao.updateTicketStatus(slip.id, "CASHED_OUT")

    // 4. Update bet entry in Room
    val betEntry = betEntryDao.getBetEntryById(slip.ticketNumber)
    if (betEntry != null) {
      betEntryDao.updateBetEntry(
        betEntry.copy(
          status = "CASHED_OUT",
          resolvedAt = now
        )
      )
    }

    Result.success(cashoutAmount)
  }

  override suspend fun evaluateAndSettleSlips(
    currentMatches: List<Match>,
    userId: String
  ): Int = withContext(Dispatchers.IO) {
    val pendingTickets = ticketDao.getPendingTickets()
    if (pendingTickets.isEmpty()) return@withContext 0

    var settledCount = 0

    for (ticket in pendingTickets) {
      val selections = parseSelections(ticket.selectionsJson)
      var allFinished = true
      var hasLost = false

      for (sel in selections) {
        val match = currentMatches.firstOrNull { it.id == sel.matchId }
          ?: currentMatches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && (it.status == MatchStatus.SCHEDULED || it.status == MatchStatus.UPCOMING || it.status == MatchStatus.LIVE) }
          ?: currentMatches.firstOrNull { (it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams) && it.status != MatchStatus.FINISHED }
          ?: currentMatches.firstOrNull { it.homeTeam in sel.matchTeams || it.awayTeam in sel.matchTeams }

        if (match == null || match.status != MatchStatus.FINISHED) {
          allFinished = false
          break
        }

        if (!evaluateSelectionWon(sel, match)) {
          hasLost = true
          break
        }
      }

      if (hasLost) {
        ticketDao.updateTicket(ticket.copy(status = "LOST"))
        settledCount++
      } else if (allFinished) {
        // Ticket Won! Credit full potential points
        val currentBalance = getAvailableBalance(userId)
        val newBalance = currentBalance + ticket.potentialPoints
        val now = System.currentTimeMillis()

        userBalanceDao.insertOrUpdateBalance(
          UserBalance(userId = userId, balance = newBalance, lastUpdated = now)
        )
        val wallet = walletDao.getWallet() ?: WalletEntity(id = 1)
        walletDao.insertOrUpdateWallet(
          wallet.copy(
            availablePoints = newBalance,
            lifetimeWon = wallet.lifetimeWon + ticket.potentialPoints
          )
        )
        walletDao.insertTransaction(
          TransactionEntity(
            type = "BET_WIN",
            amount = ticket.potentialPoints,
            balanceAfter = newBalance,
            description = "Kupon Kazancı (#${ticket.ticketNumber}) 🎉",
            createdAt = now
          )
        )
        ticketDao.updateTicket(ticket.copy(status = "WON"))
        settledCount++
      }
    }

    settledCount
  }

  override suspend fun addBonusTp(
    amount: Long,
    description: String,
    userId: String
  ): Long = withContext(Dispatchers.IO) {
    val currentBalance = getAvailableBalance(userId)
    val newBalance = currentBalance + amount
    val now = System.currentTimeMillis()

    userBalanceDao.insertOrUpdateBalance(
      UserBalance(userId = userId, balance = newBalance, lastUpdated = now)
    )
    val wallet = walletDao.getWallet() ?: WalletEntity(id = 1)
    walletDao.insertOrUpdateWallet(
      wallet.copy(availablePoints = newBalance)
    )
    walletDao.insertTransaction(
      TransactionEntity(
        type = "BONUS_REWARD",
        amount = amount,
        balanceAfter = newBalance,
        description = description,
        createdAt = now
      )
    )
    newBalance
  }

  private fun evaluateSelectionWon(sel: SlipSelection, match: Match): Boolean {
    return when (sel.marketType) {
      MarketType.MATCH_RESULT, MarketType.BASKETBALL_MS, MarketType.MOTORSPORTS_WINNER -> {
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
        val both = match.homeScore > 0 && match.awayScore > 0
        if (sel.selectionName.contains("Var", ignoreCase = true)) both else !both
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
  }

  private fun mapEntityToSimulatedBetSlip(entity: TicketEntity): SimulatedBetSlip {
    return SimulatedBetSlip(
      id = entity.id,
      ticketNumber = entity.ticketNumber,
      type = entity.type,
      stakePoints = entity.stakePoints,
      totalOdds = entity.totalOdds,
      potentialPoints = entity.potentialPoints,
      status = entity.status,
      createdAt = entity.createdAt,
      selectionsJson = entity.selectionsJson,
      selections = parseSelections(entity.selectionsJson)
    )
  }

  private fun parseSelections(json: String): List<SlipSelection> {
    if (json.isBlank()) return emptyList()
    return try {
      val arr = JSONArray(json)
      val list = mutableListOf<SlipSelection>()
      for (i in 0 until arr.length()) {
        val obj = arr.getJSONObject(i)
        list.add(
          SlipSelection(
            matchId = obj.optString("matchId"),
            matchTeams = obj.optString("matchTeams"),
            marketType = try {
              MarketType.valueOf(obj.optString("marketType"))
            } catch (_: Exception) {
              MarketType.MATCH_RESULT
            },
            selectionId = obj.optString("selectionId"),
            selectionName = obj.optString("selectionName"),
            odd = obj.optDouble("odd", 1.0),
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
