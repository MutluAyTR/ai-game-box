package com.example.ui.wallet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.TransactionEntity
import com.example.data.local.WalletEntity
import com.example.data.model.Ticket
import com.example.data.model.TicketStatus
import com.example.data.model.TicketType
import com.example.data.model.TransactionType
import com.example.data.model.UserWallet
import com.example.data.model.WalletTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WalletViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val walletDao = database.walletDao()
  private val ticketDao = database.ticketDao()

  private val _actionFeedback = MutableStateFlow<String?>(null)
  val actionFeedback: StateFlow<String?> = _actionFeedback.asStateFlow()

  // High-level UserWallet model observable flow
  val userWallet: StateFlow<UserWallet> = combine(
    walletDao.getWalletFlow(),
    ticketDao.getAllTicketsFlow()
  ) { entity, tickets ->
    val walletEntity = entity ?: WalletEntity(id = 1, availablePoints = 10000L)
    val totalBets = tickets.size
    val wonBets = tickets.count { it.status == "WON" }

    val rank = when {
      wonBets >= 15 -> "Efsane Tahminci 🏆"
      wonBets >= 8 -> "Usta Analist ⭐"
      wonBets >= 3 -> "Yarı Profesyonel 🎯"
      totalBets >= 1 -> "Gelişen Oyuncu ⚽"
      else -> "Çaylak Tahminci 🌱"
    }

    UserWallet(
      userId = "user_aycadogan",
      availablePoints = walletEntity.availablePoints,
      lockedPoints = walletEntity.lockedPoints,
      lifetimeWon = walletEntity.lifetimeWon,
      lifetimeLost = walletEntity.lifetimeLost,
      totalBetsPlaced = totalBets,
      totalBetsWon = wonBets,
      lastDailyClaimDate = walletEntity.lastDailyClaimDate,
      rankTitle = rank
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = UserWallet()
  )

  // Transactions mapped to WalletTransaction model
  val transactions: StateFlow<List<WalletTransaction>> = walletDao.getRecentTransactionsFlow()
    .combine(MutableStateFlow(Unit)) { entities, _ ->
      entities.map { entity ->
        val type = try {
          TransactionType.valueOf(entity.type)
        } catch (_: Exception) {
          TransactionType.BET_STAKE
        }
        WalletTransaction(
          id = entity.id,
          type = type,
          amount = entity.amount,
          balanceAfter = entity.balanceAfter,
          description = entity.description,
          timestamp = entity.createdAt
        )
      }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  /**
   * Simulated Betting Transaction: Deducts stake and records transaction
   */
  fun placeSimulatedBet(stake: Long, ticketSummary: String): Result<Long> {
    val currentWallet = userWallet.value
    if (stake <= 0) {
      val error = "Geçersiz bahis miktarı!"
      _actionFeedback.value = error
      return Result.failure(IllegalArgumentException(error))
    }
    if (currentWallet.availablePoints < stake) {
      val error = "Yetersiz TP Bakiyesi! (Mevcut: ${currentWallet.availablePoints} TP, Gerekli: $stake TP)"
      _actionFeedback.value = error
      return Result.failure(IllegalStateException(error))
    }

    val newBalance = currentWallet.availablePoints - stake
    val newLocked = currentWallet.lockedPoints + stake

    viewModelScope.launch(Dispatchers.IO) {
      val entity = WalletEntity(
        id = 1,
        availablePoints = newBalance,
        lockedPoints = newLocked,
        lifetimeWon = currentWallet.lifetimeWon,
        lifetimeLost = currentWallet.lifetimeLost,
        lastDailyClaimDate = currentWallet.lastDailyClaimDate
      )
      walletDao.insertOrUpdateWallet(entity)
      walletDao.insertTransaction(
        TransactionEntity(
          type = TransactionType.BET_STAKE.name,
          amount = stake,
          balanceAfter = newBalance,
          description = "Kupon Yatırıldı: $ticketSummary (-$stake TP)",
          createdAt = System.currentTimeMillis()
        )
      )
    }

    _actionFeedback.value = "Kupon bedeli ($stake TP) cüzdandan ayrıldı."
    return Result.success(newBalance)
  }

  /**
   * Simulated Win Settlement Transaction: Credits potential points to available balance
   */
  fun settleWinningBet(ticketNumber: String, stake: Long, winAmount: Long) {
    val currentWallet = userWallet.value
    val newBalance = currentWallet.availablePoints + winAmount
    val newLocked = maxOf(0L, currentWallet.lockedPoints - stake)
    val newLifetimeWon = currentWallet.lifetimeWon + winAmount

    viewModelScope.launch(Dispatchers.IO) {
      val entity = WalletEntity(
        id = 1,
        availablePoints = newBalance,
        lockedPoints = newLocked,
        lifetimeWon = newLifetimeWon,
        lifetimeLost = currentWallet.lifetimeLost,
        lastDailyClaimDate = currentWallet.lastDailyClaimDate
      )
      walletDao.insertOrUpdateWallet(entity)
      walletDao.insertTransaction(
        TransactionEntity(
          type = TransactionType.BET_WIN.name,
          amount = winAmount,
          balanceAfter = newBalance,
          description = "Kupon Kazandı: #$ticketNumber (+${winAmount} TP) 🎉",
          createdAt = System.currentTimeMillis()
        )
      )
    }
    _actionFeedback.value = "Tebrikler! #$ticketNumber kuponundan $winAmount TP kazandınız!"
  }

  /**
   * Daily bonus reward claim
   */
  fun claimDailyBonus(): Boolean {
    val current = userWallet.value
    val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    if (current.lastDailyClaimDate == today) {
      _actionFeedback.value = "Bugünkü günlük giriş ödülünüzü zaten aldınız. Yarın tekrar gelin! ⏰"
      return false
    }

    val bonus = 250L
    val newBalance = current.availablePoints + bonus

    viewModelScope.launch(Dispatchers.IO) {
      val entity = WalletEntity(
        id = 1,
        availablePoints = newBalance,
        lockedPoints = current.lockedPoints,
        lifetimeWon = current.lifetimeWon,
        lifetimeLost = current.lifetimeLost,
        lastDailyClaimDate = today
      )
      walletDao.insertOrUpdateWallet(entity)
      walletDao.insertTransaction(
        TransactionEntity(
          type = TransactionType.DAILY_REWARD.name,
          amount = bonus,
          balanceAfter = newBalance,
          description = "Günlük Giriş Ödülü (+250 TP) 🎁",
          createdAt = System.currentTimeMillis()
        )
      )
    }

    _actionFeedback.value = "Harika! +250 TP Günlük Giriş Bonusu cüzdanınıza eklendi!"
    return true
  }

  fun clearFeedback() {
    _actionFeedback.value = null
  }
}
