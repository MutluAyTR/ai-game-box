package com.example.service

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.BetEntry
import com.example.data.local.TicketEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserBalance
import com.example.data.local.UserBalanceManager
import com.example.data.local.WalletEntity
import com.example.data.model.SlipSelection
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Result sealed class representing the outcome of a betting transaction.
 */
sealed class BettingTransactionResult {
  data class Success(
    val ticket: TicketEntity,
    val remainingBalance: Long,
    val message: String
  ) : BettingTransactionResult()

  data class InsufficientBalance(
    val currentBalance: Long,
    val requiredStake: Long,
    val message: String = "Yetersiz bakiye! Kupon oynamak için $requiredStake TP gerekiyor. Mevcut bakiyeniz: $currentBalance TP."
  ) : BettingTransactionResult()

  data class InvalidStake(
    val message: String = "Geçersiz bahis miktarı! Minimum bahis 10 TP olmalıdır."
  ) : BettingTransactionResult()

  data class Error(
    val exception: Throwable,
    val message: String
  ) : BettingTransactionResult()
}

/**
 * BettingTransactionService
 *
 * Handles betting transactions:
 * 1. Validates user's virtual currency balance (TP - Tahmin Puanı) against the requested stake.
 * 2. Deducts stake atomically and updates Room database records across `tickets`, `bet_entries`,
 *    `wallet`, and `wallet_transactions`.
 * 3. Advances daily mission quests for Kupon achievements.
 * 4. Resolves settled bets and credits virtual winnings to user balance.
 */
class BettingTransactionService(private val database: AppDatabase) {

  private val ticketDao = database.ticketDao()
  private val walletDao = database.walletDao()
  private val userBalanceDao = database.userBalanceDao()
  private val betEntryDao = database.betEntryDao()
  private val userStatisticsDao = database.userStatisticsDao()
  private val balanceManager = UserBalanceManager(database)

  companion object {
    @Volatile
    private var INSTANCE: BettingTransactionService? = null

    fun getInstance(context: Context): BettingTransactionService {
      return INSTANCE ?: synchronized(this) {
        val db = AppDatabase.getDatabase(context.applicationContext)
        val instance = BettingTransactionService(db)
        INSTANCE = instance
        instance
      }
    }
  }

  /**
   * Flow of user's active (pending) tickets from Room database.
   */
  fun getActiveBetsFlow(): Flow<List<TicketEntity>> {
    return ticketDao.getTicketsByStatusFlow("PENDING")
      .distinctUntilChanged()
      .flowOn(Dispatchers.IO)
  }

  /**
   * Flow of all betting history from Room database.
   */
  fun getAllBetsFlow(): Flow<List<TicketEntity>> {
    return ticketDao.getAllTicketsFlow()
      .distinctUntilChanged()
      .flowOn(Dispatchers.IO)
  }

  /**
   * Flow of user's current virtual balance (TP) from Room database.
   */
  fun getVirtualBalanceFlow(userId: String = "default_user"): Flow<Long> {
    return balanceManager.getAvailablePointsFlow(userId)
  }

  /**
   * Flow of recent wallet ledger transactions from Room database.
   */
  fun getRecentTransactionsFlow(): Flow<List<TransactionEntity>> {
    return walletDao.getRecentTransactionsFlow()
      .distinctUntilChanged()
      .flowOn(Dispatchers.IO)
  }

  /**
   * Places a virtual bet, validating user's balance and updating Room database records accordingly.
   */
  suspend fun placeBet(
    type: String = "KOMBİNE",
    stakePoints: Long,
    totalOdds: Double,
    selections: List<SlipSelection>,
    userId: String = "default_user"
  ): BettingTransactionResult = withContext(Dispatchers.IO) {
    if (stakePoints < 10) {
      return@withContext BettingTransactionResult.InvalidStake("Minimum bahis miktarı 10 TP'dir.")
    }

    val currentBalance = balanceManager.getAvailablePoints(userId)
    if (currentBalance < stakePoints) {
      return@withContext BettingTransactionResult.InsufficientBalance(
        currentBalance = currentBalance,
        requiredStake = stakePoints
      )
    }

    try {
      // 1. Deduct stake from user balance & wallet in Room
      val deductionSuccess = balanceManager.deductStake(
        amount = stakePoints,
        description = "Kupon Oynandı ($type - ${selections.size} Maç) • Toplam Oran: ${String.format("%.2f", totalOdds)}",
        userId = userId
      )

      if (!deductionSuccess) {
        return@withContext BettingTransactionResult.InsufficientBalance(
          currentBalance = balanceManager.getAvailablePoints(userId),
          requiredStake = stakePoints
        )
      }

      val remainingBalance = balanceManager.getAvailablePoints(userId)
      val ticketNumber = "TA-${(System.currentTimeMillis() % 1_000_000)}"
      val potentialPoints = (stakePoints * totalOdds).toLong()

      // Convert selections to JSON for structured persistence
      val selectionsJson = JSONArray().apply {
        selections.forEach { sel ->
          put(
            JSONObject().apply {
              put("matchId", sel.matchId)
              put("matchTeams", sel.matchTeams)
              put("marketType", sel.marketType.name)
              put("selectionId", sel.selectionId)
              put("selectionName", sel.selectionName)
              put("odd", sel.odd)
              put("isLive", sel.isLive)
            }
          )
        }
      }.toString()

      // 2. Insert Ticket into Room database
      val ticketEntity = TicketEntity(
        ticketNumber = ticketNumber,
        type = type,
        stakePoints = stakePoints,
        totalOdds = totalOdds,
        potentialPoints = potentialPoints,
        status = "PENDING",
        createdAt = System.currentTimeMillis(),
        selectionsJson = selectionsJson
      )
      val insertedId = ticketDao.insertTicket(ticketEntity)
      val persistedTicket = ticketEntity.copy(id = insertedId)

      // 3. Insert into BetEntry table for cross-compatibility
      val matchSummary = if (selections.size == 1) {
        selections[0].matchTeams
      } else {
        "${selections.size}'li Kombine Kupon (${selections.first().matchTeams} vb.)"
      }
      betEntryDao.insertBetEntry(
        BetEntry(
          betId = ticketNumber,
          userId = userId,
          stakeAmount = stakePoints,
          totalOdds = totalOdds,
          potentialReturn = potentialPoints,
          status = "PENDING",
          placedAt = System.currentTimeMillis(),
          matchSummary = matchSummary,
          selectionsJson = selectionsJson
        )
      )

      // 4. Advance Daily Missions
      balanceManager.incrementMissionProgress("mission_3_bets", 1)
      if (totalOdds >= 3.0) {
        balanceManager.incrementMissionProgress("mission_high_odds", 1)
      }
      if (selections.size >= 2) {
        balanceManager.incrementMissionProgress("mission_2_sports", 1)
      }

      return@withContext BettingTransactionResult.Success(
        ticket = persistedTicket,
        remainingBalance = remainingBalance,
        message = "Kuponunuz başarıyla onaylandı! #$ticketNumber (Olası Kazanç: $potentialPoints TP)"
      )
    } catch (e: Exception) {
      return@withContext BettingTransactionResult.Error(
        exception = e,
        message = "Bahis kaydedilirken bir hata oluştu: ${e.localizedMessage}"
      )
    }
  }

  /**
   * Resolves a bet when matches complete, updating the database records accordingly.
   */
  suspend fun resolveBet(
    ticketId: Long,
    isWon: Boolean,
    userId: String = "default_user"
  ): Boolean = withContext(Dispatchers.IO) {
    val ticket = ticketDao.getTicketById(ticketId) ?: return@withContext false
    if (ticket.status != "PENDING") return@withContext false

    val newStatus = if (isWon) "WON" else "LOST"
    ticketDao.updateTicketStatus(ticketId, newStatus)

    // Update corresponding BetEntry
    val betEntry = betEntryDao.getBetEntryById(ticket.ticketNumber)
    if (betEntry != null) {
      betEntryDao.updateBetEntry(
        betEntry.copy(
          status = newStatus,
          resolvedAt = System.currentTimeMillis()
        )
      )
    }

    if (isWon) {
      val payout = ticket.potentialPoints
      balanceManager.addVirtualBalance(
        amount = payout,
        description = "Kupon Kazandı 🏆 #${ticket.ticketNumber} (+${payout} TP)",
        userId = userId
      )
      balanceManager.incrementMissionProgress("mission_win_1", 1)
      balanceManager.recordBetResultInStats(userId = userId, isWon = true, tpWon = payout)
    } else {
      balanceManager.recordBetResultInStats(userId = userId, isWon = false, tpWon = 0L)
    }

    return@withContext true
  }

  /**
   * Performs Cash-Out on an active bet with a calculated fair payout.
   */
  suspend fun cashOutBet(
    ticketId: Long,
    cashOutOfferRatio: Double = 0.85,
    userId: String = "default_user",
    overrideAmount: Long? = null
  ): Long = withContext(Dispatchers.IO) {
    val ticket = ticketDao.getTicketById(ticketId) ?: return@withContext 0L
    if (ticket.status != "PENDING") return@withContext 0L

    val cashOutAmount = overrideAmount ?: ((ticket.potentialPoints * cashOutOfferRatio).toLong().coerceAtLeast(ticket.stakePoints / 2))
    ticketDao.updateTicketStatus(ticketId, "CASHOUT")

    balanceManager.addVirtualBalance(
      amount = cashOutAmount,
      description = "Erken Bahis Bozdurma (Cash-Out) 💰 #${ticket.ticketNumber} (+${cashOutAmount} TP)",
      userId = userId
    )

    return@withContext cashOutAmount
  }
}
