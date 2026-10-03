package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BetEntry
import com.example.data.local.TransactionEntity
import com.example.data.local.UserBalance
import com.example.data.local.WalletEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to manage the user's virtual TP (Tahmin Puanı) balance
 * and provide methods for updating the balance after placing a bet.
 */
class VirtualTpViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  private val walletDao = database.walletDao()
  private val userBalanceDao = database.userBalanceDao()
  private val betEntryDao = database.betEntryDao()

  // Real-time Virtual TP (Tahmin Puanı) balance flow
  val tpBalance: StateFlow<Long> = walletDao.getWalletFlow()
    .map { it?.availablePoints ?: 10000L }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000L)

  val walletState: StateFlow<WalletEntity?> = walletDao.getWalletFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val recentTransactions: StateFlow<List<TransactionEntity>> = walletDao.getRecentTransactionsFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeBetEntries: StateFlow<List<BetEntry>> = betEntryDao.getBetEntriesFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _operationStatusMessage = MutableStateFlow<String?>(null)
  val operationStatusMessage: StateFlow<String?> = _operationStatusMessage.asStateFlow()

  init {
    ensureInitialBalanceSeeded()
  }

  private fun ensureInitialBalanceSeeded() {
    viewModelScope.launch(Dispatchers.IO) {
      val existing = walletDao.getWallet()
      if (existing == null) {
        val initial = WalletEntity(
          id = 1,
          availablePoints = 10000L,
          lockedPoints = 0L,
          lifetimeWon = 0L,
          lifetimeLost = 0L
        )
        walletDao.insertOrUpdateWallet(initial)
        walletDao.insertTransaction(
          TransactionEntity(
            type = "WELCOME_BONUS",
            amount = 10000L,
            balanceAfter = 10000L,
            description = "Hoş Geldin Bonusu: 10.000 TP Başlangıç Bakiyesi 🎁",
            createdAt = System.currentTimeMillis()
          )
        )
      }

      val existingUserBalance = userBalanceDao.getUserBalance("default_user")
      if (existingUserBalance == null) {
        userBalanceDao.insertOrUpdateBalance(
          UserBalance(
            userId = "default_user",
            balance = 10000L,
            currencyCode = "TP"
          )
        )
      }
    }
  }

  /**
   * Updates the virtual TP balance after placing a bet.
   * Validates sufficient balance, deducts the stake, records transaction and bet entry.
   *
   * @param stakeAmount The amount of TP to wager
   * @param totalOdds The total odds of the placed bet
   * @param matchSummary Summary description of selected matches
   * @return Result containing the created BetEntry on success or Exception on failure
   */
  fun placeBet(
    stakeAmount: Long,
    totalOdds: Double = 1.0,
    matchSummary: String = "Sanal Spor Bahsi",
    selectionsJson: String = ""
  ): Result<BetEntry> {
    if (stakeAmount <= 0) {
      _operationStatusMessage.value = "⚠️ Geçersiz bahis tutarı: En az 1 TP girilmelidir."
      return Result.failure(IllegalArgumentException("Bahis tutarı en az 1 TP olmalıdır."))
    }

    val currentBalance = tpBalance.value
    if (currentBalance < stakeAmount) {
      _operationStatusMessage.value = "⚠️ Yetersiz Bakiye! Mevcut TP: $currentBalance, Gereken: $stakeAmount"
      return Result.failure(IllegalStateException("Yetersiz TP Bakiyesi"))
    }

    val potentialReturn = (stakeAmount * totalOdds).toLong()
    val betId = "BET-${System.currentTimeMillis()}"

    val betEntry = BetEntry(
      betId = betId,
      userId = "default_user",
      stakeAmount = stakeAmount,
      totalOdds = totalOdds,
      potentialReturn = potentialReturn,
      status = "PENDING",
      matchSummary = matchSummary,
      selectionsJson = selectionsJson
    )

    viewModelScope.launch(Dispatchers.IO) {
      val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      if (currentWallet.availablePoints >= stakeAmount) {
        val newBalance = currentWallet.availablePoints - stakeAmount
        walletDao.insertOrUpdateWallet(
          currentWallet.copy(
            availablePoints = newBalance,
            lifetimeLost = currentWallet.lifetimeLost + stakeAmount
          )
        )
        walletDao.insertTransaction(
          TransactionEntity(
            type = "BET_STAKE",
            amount = -stakeAmount,
            balanceAfter = newBalance,
            description = "Kupon Yatırıldı (#$betId) - Oran: %.2f".format(totalOdds),
            createdAt = System.currentTimeMillis()
          )
        )

        userBalanceDao.updateBalance("default_user", newBalance, System.currentTimeMillis())
        betEntryDao.insertBetEntry(betEntry)

        _operationStatusMessage.value = "✅ Kupon onaylandı! -$stakeAmount TP düşüldü. (Kalan: $newBalance TP)"
      }
    }

    return Result.success(betEntry)
  }

  /**
   * Direct method to update balance after placing a bet (convenience method).
   */
  fun updateBalanceAfterBet(stakeAmount: Long): Boolean {
    val currentBalance = tpBalance.value
    if (stakeAmount <= 0 || currentBalance < stakeAmount) return false

    viewModelScope.launch(Dispatchers.IO) {
      val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      if (currentWallet.availablePoints >= stakeAmount) {
        val newBalance = currentWallet.availablePoints - stakeAmount
        walletDao.insertOrUpdateWallet(
          currentWallet.copy(
            availablePoints = newBalance,
            lifetimeLost = currentWallet.lifetimeLost + stakeAmount
          )
        )
        walletDao.insertTransaction(
          TransactionEntity(
            type = "BET_STAKE",
            amount = -stakeAmount,
            balanceAfter = newBalance,
            description = "Bahis Oynandı (-$stakeAmount TP)",
            createdAt = System.currentTimeMillis()
          )
        )
        userBalanceDao.updateBalance("default_user", newBalance, System.currentTimeMillis())
      }
    }
    return true
  }

  /**
   * Credits winnings to virtual TP balance when a bet is won.
   */
  fun creditWinnings(winningAmount: Long, description: String = "Kupon Kazancı 🎉") {
    if (winningAmount <= 0) return
    viewModelScope.launch(Dispatchers.IO) {
      val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      val newBalance = currentWallet.availablePoints + winningAmount
      walletDao.insertOrUpdateWallet(
        currentWallet.copy(
          availablePoints = newBalance,
          lifetimeWon = currentWallet.lifetimeWon + winningAmount
        )
      )
      walletDao.insertTransaction(
        TransactionEntity(
          type = "BET_WIN",
          amount = winningAmount,
          balanceAfter = newBalance,
          description = description,
          createdAt = System.currentTimeMillis()
        )
      )
      userBalanceDao.updateBalance("default_user", newBalance, System.currentTimeMillis())
      _operationStatusMessage.value = "🎉 Tebrikler! +$winningAmount TP hesabınıza aktarıldı!"
    }
  }

  /**
   * Adds daily bonus to virtual balance.
   */
  fun claimDailyBonus(amount: Long = 500L) {
    viewModelScope.launch(Dispatchers.IO) {
      val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      val newBalance = currentWallet.availablePoints + amount
      walletDao.insertOrUpdateWallet(currentWallet.copy(availablePoints = newBalance))
      walletDao.insertTransaction(
        TransactionEntity(
          type = "DAILY_REWARD",
          amount = amount,
          balanceAfter = newBalance,
          description = "Günlük Giriş Bonusu 🎁",
          createdAt = System.currentTimeMillis()
        )
      )
      userBalanceDao.updateBalance("default_user", newBalance, System.currentTimeMillis())
      _operationStatusMessage.value = "🎁 Günlük bonusunuz alındı: +$amount TP!"
    }
  }

  /**
   * Free deposit / reward recharge.
   */
  fun depositTp(amount: Long) {
    if (amount <= 0) return
    viewModelScope.launch(Dispatchers.IO) {
      val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
      val newBalance = currentWallet.availablePoints + amount
      walletDao.insertOrUpdateWallet(currentWallet.copy(availablePoints = newBalance))
      walletDao.insertTransaction(
        TransactionEntity(
          type = "DEPOSIT",
          amount = amount,
          balanceAfter = newBalance,
          description = "Bakiye Yükleme (+${amount} TP)",
          createdAt = System.currentTimeMillis()
        )
      )
      userBalanceDao.updateBalance("default_user", newBalance, System.currentTimeMillis())
      _operationStatusMessage.value = "💳 +$amount TP bakiye başarıyla yüklendi!"
    }
  }

  fun clearStatusMessage() {
    _operationStatusMessage.value = null
  }
}
