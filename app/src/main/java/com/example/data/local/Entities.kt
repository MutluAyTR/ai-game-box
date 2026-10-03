package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val ticketNumber: String,
  val type: String, // TEKLI, KOMBINE, SISTEM
  val stakePoints: Long,
  val totalOdds: Double,
  val potentialPoints: Long,
  val status: String, // PENDING, WON, LOST
  val createdAt: Long,
  val selectionsJson: String
)

@Entity(tableName = "wallet_transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val type: String, // WELCOME_BONUS, DAILY_REWARD, BET_STAKE, BET_WIN
  val amount: Long,
  val balanceAfter: Long,
  val description: String,
  val createdAt: Long
)

@Entity(tableName = "wallet")
data class WalletEntity(
  @PrimaryKey val id: Int = 1,
  val availablePoints: Long = 10000L,
  val lockedPoints: Long = 0L,
  val lifetimeWon: Long = 0L,
  val lifetimeLost: Long = 0L,
  val lastDailyClaimDate: String = ""
)

/**
 * UserBalance entity representing the user's local virtual currency balance.
 */
@Entity(tableName = "user_balance")
data class UserBalance(
  @PrimaryKey val userId: String = "default_user",
  val balance: Long = 10000L,
  val currencyCode: String = "TP", // Tahmin Puanı
  val lockedBalance: Long = 0L,
  val totalWon: Long = 0L,
  val totalLost: Long = 0L,
  val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * BetEntry entity representing the user's betting history.
 */
@Entity(tableName = "bet_entries")
data class BetEntry(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val betId: String,
  val userId: String = "default_user",
  val stakeAmount: Long,
  val totalOdds: Double,
  val potentialReturn: Long,
  val status: String, // PENDING, WON, LOST, CANCELLED
  val placedAt: Long = System.currentTimeMillis(),
  val resolvedAt: Long? = null,
  val matchSummary: String = "",
  val selectionsJson: String = ""
)

/**
 * MatchEntity representing a sports match fixture for local Room caching.
 */
@Entity(tableName = "matches")
data class MatchEntity(
  @PrimaryKey val id: String,
  val sport: String,
  val league: String,
  val homeTeam: String,
  val awayTeam: String,
  val homeScore: Int = 0,
  val awayScore: Int = 0,
  val minute: Int = 0,
  val status: String, // SCHEDULED, UPCOMING, LIVE, HALFTIME, FINISHED
  val startTime: String,
  val matchDate: String,
  val matchDateIso: String,
  val week: Int = 1,
  val isHot: Boolean = false,
  val referee: String = "",
  val stadium: String = "",
  val weather: String = "",
  val tvBroadcast: String = "",
  val halfTimeHomeScore: Int = 0,
  val halfTimeAwayScore: Int = 0,
  val extraTimeMinutes: Int = 0,
  val isFavorite: Boolean = false
)

/**
 * LeagueEntity representing a sports competition/league.
 */
@Entity(tableName = "leagues")
data class LeagueEntity(
  @PrimaryKey val id: String,
  val name: String,
  val country: String,
  val sport: String,
  val currentWeek: Int,
  val totalTeams: Int = 20,
  val iconEmoji: String = "🏆"
)

/**
 * OddsEntity representing betting market selections and prices.
 */
@Entity(
  tableName = "odds",
  primaryKeys = ["matchId", "marketType", "selectionId"]
)
data class OddsEntity(
  val matchId: String,
  val marketType: String,
  val marketName: String,
  val selectionId: String,
  val selectionName: String,
  val odd: Double,
  val isSuspended: Boolean = false
)

/**
 * UserStatistics entity for tracking virtual TP betting performance, win rates, and community leaderboard.
 */
@Entity(tableName = "user_statistics")
data class UserStatistics(
  @PrimaryKey val userId: String,
  val username: String,
  val avatarEmoji: String = "🏆",
  val totalBets: Int = 0,
  val wonBets: Int = 0,
  val lostBets: Int = 0,
  val winRate: Double = 0.0,
  val totalTpWon: Long = 0L,
  val rank: Int = 1,
  val badge: String = "Usta Analist",
  val roiPercent: Double = 0.0
)

/**
 * DailyMissionEntity for tracking user's daily quests and virtual TP bonus rewards in Room.
 */
@Entity(tableName = "daily_missions")
data class DailyMissionEntity(
  @PrimaryKey val id: String,
  val title: String,
  val description: String,
  val rewardTp: Long,
  val currentProgress: Int = 0,
  val targetProgress: Int = 1,
  val isCompleted: Boolean = false,
  val isClaimed: Boolean = false,
  val category: String = "KUPON",
  val icon: String = "🎯"
)

