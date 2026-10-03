package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for 'Virtual TP' (Fake Currency) to handle user's balance and transaction history in the betting simulation.
 */
class VirtualTpViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val walletDao = database.walletDao()

  val walletFlow: StateFlow<WalletEntity> = walletDao.getWalletFlow()
    .map { it ?: WalletEntity(id = 1, availablePoints = 10000L) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = WalletEntity(id = 1, availablePoints = 10000L)
    )

  val transactionsFlow: StateFlow<List<TransactionEntity>> = walletDao.getRecentTransactionsFlow()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _operationMessage = MutableStateFlow<String?>(null)
  val operationMessage: StateFlow<String?> = _operationMessage.asStateFlow()

  init {
    ensureInitialBalance()
  }

  private fun ensureInitialBalance() {
    viewModelScope.launch(Dispatchers.IO) {
      val existing = walletDao.getWallet()
      if (existing == null) {
        val initialWallet = WalletEntity(
          id = 1,
          availablePoints = 10000L,
          lockedPoints = 0L,
          lifetimeWon = 0L,
          lifetimeLost = 0L
        )
        walletDao.insertOrUpdateWallet(initialWallet)
        walletDao.insertTransaction(
          TransactionEntity(
            type = "WELCOME_BONUS",
            amount = 10000L,
            balanceAfter = 10000L,
            description = "Tahmin Arena Hoş Geldin Bonusu 🎁",
            createdAt = System.currentTimeMillis()
          )
        )
      }
    }
  }

  fun depositPoints(amount: Long, source: String = "TP Yükleme") {
    viewModelScope.launch(Dispatchers.IO) {
      val current = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      val newBalance = current.availablePoints + amount
      val updated = current.copy(availablePoints = newBalance)
      walletDao.insertOrUpdateWallet(updated)
      walletDao.insertTransaction(
        TransactionEntity(
          type = "DEPOSIT",
          amount = amount,
          balanceAfter = newBalance,
          description = "$source: +$amount TP",
          createdAt = System.currentTimeMillis()
        )
      )
      _operationMessage.value = "+$amount TP hesabınıza eklendi!"
    }
  }

  fun claimDailyBonus(): Boolean {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val current = walletFlow.value
    if (current.lastDailyClaimDate == today) {
      _operationMessage.value = "Bugünkü günlük bonusunuzu zaten aldınız. Yarın tekrar gelin! ⏰"
      return false
    }

    viewModelScope.launch(Dispatchers.IO) {
      val bonus = 250L
      val newBalance = current.availablePoints + bonus
      val updated = current.copy(
        availablePoints = newBalance,
        lastDailyClaimDate = today
      )
      walletDao.insertOrUpdateWallet(updated)
      walletDao.insertTransaction(
        TransactionEntity(
          type = "DAILY_REWARD",
          amount = bonus,
          balanceAfter = newBalance,
          description = "Günlük Giriş Bonusu 🎁",
          createdAt = System.currentTimeMillis()
        )
      )
      _operationMessage.value = "Tebrikler! +250 TP Günlük Giriş Bonusu kazandınız!"
    }
    return true
  }

  /**
   * Updates the virtual TP balance after placing a bet.
   * Deducts the stake amount, writes a transaction log, and updates state.
   */
  fun updateBalanceAfterBet(stakeAmount: Long, description: String = "Kupon Bahsi"): Boolean {
    val current = walletFlow.value
    if (stakeAmount <= 0L || current.availablePoints < stakeAmount) {
      _operationMessage.value = "⚠️ Yetersiz TP bakiyesi!"
      return false
    }

    viewModelScope.launch(Dispatchers.IO) {
      val wallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      if (wallet.availablePoints >= stakeAmount) {
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
            description = "$description (-$stakeAmount TP)",
            createdAt = System.currentTimeMillis()
          )
        )
        _operationMessage.value = "Kupon yatırıldı! -$stakeAmount TP (Kalan: $newBalance TP)"
      }
    }
    return true
  }

  /**
   * Places a virtual bet and updates the user's virtual TP balance.
   */
  fun placeBet(
    stakeAmount: Long,
    totalOdds: Double = 1.0,
    matchSummary: String = "Sanal Spor Bahsi"
  ): Result<Long> {
    val current = walletFlow.value
    if (stakeAmount <= 0L) {
      return Result.failure(IllegalArgumentException("Bahis tutarı en az 1 TP olmalıdır."))
    }
    if (current.availablePoints < stakeAmount) {
      _operationMessage.value = "⚠️ Yetersiz TP bakiyesi!"
      return Result.failure(IllegalStateException("Yetersiz TP bakiyesi"))
    }

    val success = updateBalanceAfterBet(stakeAmount, "Kupon: $matchSummary (Oran: %.2f)".format(totalOdds))
    return if (success) {
      Result.success(current.availablePoints - stakeAmount)
    } else {
      Result.failure(IllegalStateException("Bakiye güncellenemedi."))
    }
  }

  fun clearOperationMessage() {
    _operationMessage.value = null
  }
}
