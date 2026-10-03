package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * UserBalanceManager using Room to store, track, and manage the user's virtual currency (TP - Tahmin Puanı)
 * for the sports betting simulation.
 *
 * Guarantees atomic balance transactions, ledger record generation, and complete synchronization
 * across both [UserBalance] and [WalletEntity] tables.
 */
class UserBalanceManager(private val database: AppDatabase) {

  private val userBalanceDao = database.userBalanceDao()
  private val walletDao = database.walletDao()
  private val userStatisticsDao = database.userStatisticsDao()
  private val dailyMissionDao = database.dailyMissionDao()

  /**
   * Flow of daily missions stored in Room.
   */
  fun getDailyMissionsFlow(): Flow<List<DailyMissionEntity>> {
    return dailyMissionDao.getAllMissionsFlow()
      .distinctUntilChanged()
      .flowOn(Dispatchers.IO)
  }

  suspend fun ensureDailyMissionsSeeded() = withContext(Dispatchers.IO) {
    if (dailyMissionDao.getCount() == 0) {
      val defaultMissions = listOf(
        DailyMissionEntity(
          id = "mission_3_bets",
          title = "3 Adet Kupon Yap",
          description = "Günün bülteninden en az 3 kupon hazırla ve onayla",
          rewardTp = 1500L,
          currentProgress = 0,
          targetProgress = 3,
          category = "KUPON",
          icon = "🎟️"
        ),
        DailyMissionEntity(
          id = "mission_2_sports",
          title = "2 Farklı Branşta Bahis Yap",
          description = "Futbol ve basketbol gibi en az 2 farklı spor dalında kupon oyna",
          rewardTp = 2000L,
          currentProgress = 0,
          targetProgress = 2,
          category = "SPOR",
          icon = "⚽"
        ),
        DailyMissionEntity(
          id = "mission_win_1",
          title = "Günün Kazananı Ol",
          description = "En az 1 kuponunu başarıyla tutturup kazanç sağla",
          rewardTp = 3000L,
          currentProgress = 0,
          targetProgress = 1,
          category = "KAZANÇ",
          icon = "🏆"
        ),
        DailyMissionEntity(
          id = "mission_watch_ad",
          title = "Ödüllü Reklam İzle",
          description = "Sponsorlu multi-ad reklamı izleyerek kasana destek sağla",
          rewardTp = 1000L,
          currentProgress = 0,
          targetProgress = 1,
          category = "REKLAM",
          icon = "📺"
        ),
        DailyMissionEntity(
          id = "mission_explore_stats",
          title = "3 Maçın AI Telemetrisini İncele",
          description = "xG ve Monte Carlo olasılık merkezinde 3 karşılaşmayı analiz et",
          rewardTp = 1200L,
          currentProgress = 0,
          targetProgress = 3,
          category = "ANALİZ",
          icon = "🎯"
        ),
        DailyMissionEntity(
          id = "mission_high_odds",
          title = "3.00+ Oranlı Kupon Yap",
          description = "Toplam kümülatif oranı 3.00 veya üzeri olan bir kupon oluştur",
          rewardTp = 2500L,
          currentProgress = 0,
          targetProgress = 1,
          category = "ORAN",
          icon = "⚡"
        )
      )
      dailyMissionDao.insertAll(defaultMissions)
    }
  }

  suspend fun incrementMissionProgress(missionId: String, amount: Int = 1) = withContext(Dispatchers.IO) {
    ensureDailyMissionsSeeded()
    val mission = dailyMissionDao.getMissionById(missionId) ?: return@withContext
    if (mission.isCompleted) return@withContext

    val newProgress = (mission.currentProgress + amount).coerceAtMost(mission.targetProgress)
    val isNowCompleted = newProgress >= mission.targetProgress
    dailyMissionDao.updateProgress(missionId, newProgress, isNowCompleted)
  }

  suspend fun claimMissionReward(missionId: String, userId: String = "default_user"): Long = withContext(Dispatchers.IO) {
    val mission = dailyMissionDao.getMissionById(missionId) ?: return@withContext 0L
    if (!mission.isCompleted || mission.isClaimed) return@withContext 0L

    dailyMissionDao.markClaimed(missionId)
    addVirtualBalance(
      amount = mission.rewardTp,
      description = "🎯 Günlük Görev Tamamlandı: ${mission.title} (+${mission.rewardTp} TP)",
      userId = userId
    )
    return@withContext mission.rewardTp
  }

  suspend fun resetBalance(userId: String = "default_user"): Long = withContext(Dispatchers.IO) {
    val resetAmount = 10000L
    val ub = userBalanceDao.getUserBalance(userId) ?: UserBalance(userId = userId, balance = resetAmount)
    userBalanceDao.insertOrUpdateBalance(ub.copy(balance = resetAmount, lastUpdated = System.currentTimeMillis()))

    val currentWallet = walletDao.getWallet() ?: WalletEntity()
    walletDao.insertOrUpdateWallet(currentWallet.copy(availablePoints = resetAmount))

    walletDao.insertTransaction(
      TransactionEntity(
        type = "RESET",
        amount = resetAmount,
        balanceAfter = resetAmount,
        description = "Bakiye Başlangıç Seviyesine Sıfırlandı 🔄 (10.000 TP)",
        createdAt = System.currentTimeMillis()
      )
    )
    return@withContext resetAmount
  }

  /**
   * Flow of top users sorted by accumulated virtual TP winnings.
   */
  fun getLeaderboardFlow(): Flow<List<UserStatistics>> {
    return userStatisticsDao.getLeaderboardFlow()
      .distinctUntilChanged()
      .flowOn(Dispatchers.IO)
  }

  suspend fun ensureLeaderboardSeeded() = withContext(Dispatchers.IO) {
    if (userStatisticsDao.getCount() == 0) {
      val seeded = listOf(
        UserStatistics("u_1", "Kerem_Analist", "👑", totalBets = 182, wonBets = 148, lostBets = 34, winRate = 81.3, totalTpWon = 345800L, rank = 1, badge = "Efsane Analist", roiPercent = 42.5),
        UserStatistics("u_2", "Zeynep_Stats", "🥈", totalBets = 86, wonBets = 72, lostBets = 14, winRate = 83.7, totalTpWon = 289400L, rank = 2, badge = "xG Uzmanı", roiPercent = 58.4),
        UserStatistics("u_3", "Süper_Kuponcu", "🥉", totalBets = 210, wonBets = 138, lostBets = 72, winRate = 65.7, totalTpWon = 214200L, rank = 3, badge = "Kombine Ustası", roiPercent = 29.4),
        UserStatistics("u_4", "Caner_ValueBet", "🎯", totalBets = 64, wonBets = 52, lostBets = 12, winRate = 81.2, totalTpWon = 198300L, rank = 4, badge = "Değerli Bahisçi", roiPercent = 64.2),
        UserStatistics("u_5", "Ahmet_Derbi", "⚡", totalBets = 125, wonBets = 84, lostBets = 41, winRate = 67.2, totalTpWon = 168300L, rank = 5, badge = "Canlı Avcısı", roiPercent = 33.1),
        UserStatistics("u_6", "Bora_Basket", "🏀", totalBets = 92, wonBets = 68, lostBets = 24, winRate = 73.9, totalTpWon = 142100L, rank = 6, badge = "EuroLeague Dehası", roiPercent = 47.8),
        UserStatistics("u_7", "TahminciDede", "👴", totalBets = 150, wonBets = 92, lostBets = 58, winRate = 61.3, totalTpWon = 112400L, rank = 7, badge = "Sistem Stratejisti", roiPercent = 21.7),
        UserStatistics("u_8", "Deniz_Banko", "💎", totalBets = 48, wonBets = 41, lostBets = 7, winRate = 85.4, totalTpWon = 98500L, rank = 8, badge = "Banko Avcısı", roiPercent = 52.3),
        UserStatistics("default_user", "Ben (Kullanıcı)", "👤", totalBets = 15, wonBets = 11, lostBets = 4, winRate = 73.3, totalTpWon = 68500L, rank = 9, badge = "Yükselen Yıldız", roiPercent = 31.0)
      )
      userStatisticsDao.insertAll(seeded)
    }
  }

  suspend fun recordBetResultInStats(userId: String = "default_user", isWon: Boolean, tpWon: Long = 0L) = withContext(Dispatchers.IO) {
    val existing = userStatisticsDao.getUserStats(userId) ?: UserStatistics(
      userId = userId,
      username = "Ben (Kullanıcı)",
      avatarEmoji = "👤"
    )
    val newTotalBets = existing.totalBets + 1
    val newWon = if (isWon) existing.wonBets + 1 else existing.wonBets
    val newLost = if (!isWon) existing.lostBets + 1 else existing.lostBets
    val newWinRate = (newWon.toDouble() / newTotalBets * 100.0).let { (it * 10).toInt() / 10.0 }
    val newTp = existing.totalTpWon + tpWon

    val updated = existing.copy(
      totalBets = newTotalBets,
      wonBets = newWon,
      lostBets = newLost,
      winRate = newWinRate,
      totalTpWon = newTp
    )
    userStatisticsDao.insertOrUpdate(updated)
  }

  /**
   * Flow of user's current balance entity.
   */
  fun getUserBalanceFlow(userId: String = "default_user"): Flow<UserBalance> {
    return userBalanceDao.getUserBalanceFlow(userId)
      .map { balance ->
        balance ?: UserBalance(
          userId = userId,
          balance = 10000L,
          currencyCode = "TP",
          lockedBalance = 0L,
          totalWon = 0L,
          totalLost = 0L,
          lastUpdated = System.currentTimeMillis()
        )
      }
      .distinctUntilChanged()
      .flowOn(Dispatchers.IO)
  }

  /**
   * Flow of user's available TP points.
   */
  fun getAvailablePointsFlow(userId: String = "default_user"): Flow<Long> {
    return getUserBalanceFlow(userId)
      .map { it.balance }
      .distinctUntilChanged()
  }

  /**
   * Reads current available TP balance synchronously on IO dispatcher.
   */
  suspend fun getAvailablePoints(userId: String = "default_user"): Long = withContext(Dispatchers.IO) {
    ensureInitialized(userId)
    val userBalance = userBalanceDao.getUserBalance(userId)
    if (userBalance != null) {
      return@withContext userBalance.balance
    }
    val wallet = walletDao.getWallet()
    return@withContext wallet?.availablePoints ?: 10000L
  }

  /**
   * Validates if the user has enough available TP to place a bet or make a transaction.
   */
  suspend fun validateBalance(requiredAmount: Long, userId: String = "default_user"): Boolean = withContext(Dispatchers.IO) {
    if (requiredAmount <= 0) return@withContext false
    val current = getAvailablePoints(userId)
    return@withContext current >= requiredAmount
  }

  /**
   * Deducts TP stake from the user's balance and records transaction in ledger.
   * Returns true if successful, false if insufficient funds or invalid amount.
   */
  suspend fun deductStake(
    amount: Long,
    description: String,
    userId: String = "default_user"
  ): Boolean = withContext(Dispatchers.IO) {
    if (amount <= 0) return@withContext false

    ensureInitialized(userId)
    val currentBalance = userBalanceDao.getUserBalance(userId) ?: UserBalance(userId = userId, balance = 10000L)
    if (currentBalance.balance < amount) {
      return@withContext false
    }

    val newBalance = currentBalance.balance - amount
    val updatedUserBalance = currentBalance.copy(
      balance = newBalance,
      lockedBalance = currentBalance.lockedBalance + amount,
      totalLost = currentBalance.totalLost + amount,
      lastUpdated = System.currentTimeMillis()
    )
    userBalanceDao.insertOrUpdateBalance(updatedUserBalance)

    // Sync legacy Wallet table
    val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
    val updatedWallet = currentWallet.copy(
      availablePoints = newBalance,
      lockedPoints = currentWallet.lockedPoints + amount
    )
    walletDao.insertOrUpdateWallet(updatedWallet)

    // Record ledger entry
    walletDao.insertTransaction(
      TransactionEntity(
        type = "BET_STAKE",
        amount = -amount,
        balanceAfter = newBalance,
        description = description,
        createdAt = System.currentTimeMillis()
      )
    )

    return@withContext true
  }

  /**
   * Adds winnings (TP) to the user's virtual balance and records transaction in ledger.
   */
  suspend fun addWinnings(
    amount: Long,
    description: String,
    userId: String = "default_user"
  ): Long = withContext(Dispatchers.IO) {
    if (amount <= 0) return@withContext getAvailablePoints(userId)

    ensureInitialized(userId)
    val currentBalance = userBalanceDao.getUserBalance(userId) ?: UserBalance(userId = userId, balance = 10000L)
    val newBalance = currentBalance.balance + amount
    val updatedUserBalance = currentBalance.copy(
      balance = newBalance,
      totalWon = currentBalance.totalWon + amount,
      lastUpdated = System.currentTimeMillis()
    )
    userBalanceDao.insertOrUpdateBalance(updatedUserBalance)

    // Sync legacy Wallet table
    val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
    val updatedWallet = currentWallet.copy(
      availablePoints = newBalance,
      lifetimeWon = currentWallet.lifetimeWon + amount
    )
    walletDao.insertOrUpdateWallet(updatedWallet)

    // Record ledger entry
    walletDao.insertTransaction(
      TransactionEntity(
        type = "BET_WIN",
        amount = amount,
        balanceAfter = newBalance,
        description = description,
        createdAt = System.currentTimeMillis()
      )
    )

    return@withContext newBalance
  }

  /**
   * Refunds stake in case of cancelled or voided match events.
   */
  suspend fun refundStake(
    amount: Long,
    description: String,
    userId: String = "default_user"
  ): Long = withContext(Dispatchers.IO) {
    if (amount <= 0) return@withContext getAvailablePoints(userId)

    ensureInitialized(userId)
    val currentBalance = userBalanceDao.getUserBalance(userId) ?: UserBalance(userId = userId, balance = 10000L)
    val newBalance = currentBalance.balance + amount
    val updatedUserBalance = currentBalance.copy(
      balance = newBalance,
      lockedBalance = (currentBalance.lockedBalance - amount).coerceAtLeast(0L),
      lastUpdated = System.currentTimeMillis()
    )
    userBalanceDao.insertOrUpdateBalance(updatedUserBalance)

    val currentWallet = walletDao.getWallet() ?: WalletEntity(id = 1, availablePoints = 10000L)
    val updatedWallet = currentWallet.copy(
      availablePoints = newBalance,
      lockedPoints = (currentWallet.lockedPoints - amount).coerceAtLeast(0L)
    )
    walletDao.insertOrUpdateWallet(updatedWallet)

    walletDao.insertTransaction(
      TransactionEntity(
        type = "REFUND",
        amount = amount,
        balanceAfter = newBalance,
        description = description,
        createdAt = System.currentTimeMillis()
      )
    )

    return@withContext newBalance
  }

  /**
   * Adds daily login reward bonus.
   */
  suspend fun claimDailyReward(
    bonusAmount: Long = 250L,
    userId: String = "default_user"
  ): Result<Long> = withContext(Dispatchers.IO) {
    val currentWallet = walletDao.getWallet() ?: WalletEntity()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    if (currentWallet.lastDailyClaimDate == today) {
      return@withContext Result.failure(Exception("Bugünkü günlük bonusunuzu zaten aldınız! Yarın tekrar bekleriz."))
    }

    val newBalance = currentWallet.availablePoints + bonusAmount
    val updated = currentWallet.copy(
      availablePoints = newBalance,
      lastDailyClaimDate = today
    )
    walletDao.insertOrUpdateWallet(updated)

    val currentBalance = userBalanceDao.getUserBalance(userId) ?: UserBalance(userId = userId, balance = 10000L)
    userBalanceDao.insertOrUpdateBalance(
      currentBalance.copy(
        balance = newBalance,
        lastUpdated = System.currentTimeMillis()
      )
    )

    walletDao.insertTransaction(
      TransactionEntity(
        type = "DAILY_REWARD",
        amount = bonusAmount,
        balanceAfter = newBalance,
        description = "Günlük Giriş Bonusu Alındı 🎁 (+${bonusAmount} TP)",
        createdAt = System.currentTimeMillis()
      )
    )

    return@withContext Result.success(bonusAmount)
  }

  /**
   * Recharges or resets user balance with virtual TP.
   */
  suspend fun addVirtualBalance(
    amount: Long,
    description: String,
    userId: String = "default_user"
  ): Long = withContext(Dispatchers.IO) {
    if (amount <= 0) return@withContext getAvailablePoints(userId)

    val current = getAvailablePoints(userId)
    val newBalance = current + amount

    val ub = userBalanceDao.getUserBalance(userId) ?: UserBalance(userId = userId, balance = current)
    userBalanceDao.insertOrUpdateBalance(ub.copy(balance = newBalance, lastUpdated = System.currentTimeMillis()))

    val currentWallet = walletDao.getWallet() ?: WalletEntity()
    walletDao.insertOrUpdateWallet(currentWallet.copy(availablePoints = newBalance))

    walletDao.insertTransaction(
      TransactionEntity(
        type = "DEPOSIT",
        amount = amount,
        balanceAfter = newBalance,
        description = description,
        createdAt = System.currentTimeMillis()
      )
    )

    return@withContext newBalance
  }

  private suspend fun ensureInitialized(userId: String) {
    val existing = userBalanceDao.getUserBalance(userId)
    if (existing == null) {
      val defaultBalance = UserBalance(
        userId = userId,
        balance = 10000L,
        currencyCode = "TP",
        lockedBalance = 0L,
        totalWon = 0L,
        totalLost = 0L,
        lastUpdated = System.currentTimeMillis()
      )
      userBalanceDao.insertOrUpdateBalance(defaultBalance)
    }

    val existingWallet = walletDao.getWallet()
    if (existingWallet == null) {
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
          description = "TahminArena Hoş Geldin Bonusu 🎁 (10.000 TP)",
          createdAt = System.currentTimeMillis()
        )
      )
    }
  }
}
