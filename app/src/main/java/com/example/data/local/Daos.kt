package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
  @Query("SELECT * FROM tickets ORDER BY createdAt DESC")
  fun getAllTicketsFlow(): Flow<List<TicketEntity>>

  @Query("SELECT * FROM tickets WHERE status = :status ORDER BY createdAt DESC")
  fun getTicketsByStatusFlow(status: String): Flow<List<TicketEntity>>

  @Query("SELECT * FROM tickets WHERE status = 'PENDING'")
  suspend fun getPendingTickets(): List<TicketEntity>

  @Query("SELECT * FROM tickets WHERE id = :id")
  suspend fun getTicketById(id: Long): TicketEntity?

  @Query("UPDATE tickets SET status = :status WHERE id = :id")
  suspend fun updateTicketStatus(id: Long, status: String)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTicket(ticket: TicketEntity): Long

  @Update
  suspend fun updateTicket(ticket: TicketEntity)
}

@Dao
interface WalletDao {
  @Query("SELECT * FROM wallet WHERE id = 1")
  fun getWalletFlow(): Flow<WalletEntity?>

  @Query("SELECT * FROM wallet WHERE id = 1")
  suspend fun getWallet(): WalletEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateWallet(wallet: WalletEntity)

  @Query("SELECT * FROM wallet_transactions ORDER BY createdAt DESC LIMIT 50")
  fun getRecentTransactionsFlow(): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity): Long
}

@Dao
interface UserBalanceDao {
  @Query("SELECT * FROM user_balance WHERE userId = :userId")
  fun getUserBalanceFlow(userId: String = "default_user"): Flow<UserBalance?>

  @Query("SELECT * FROM user_balance WHERE userId = :userId")
  suspend fun getUserBalance(userId: String = "default_user"): UserBalance?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateBalance(userBalance: UserBalance)

  @Query("UPDATE user_balance SET balance = :newBalance, lastUpdated = :timestamp WHERE userId = :userId")
  suspend fun updateBalance(userId: String, newBalance: Long, timestamp: Long)
}

@Dao
interface BetEntryDao {
  @Query("SELECT * FROM bet_entries WHERE userId = :userId ORDER BY placedAt DESC")
  fun getBetEntriesFlow(userId: String = "default_user"): Flow<List<BetEntry>>

  @Query("SELECT * FROM bet_entries WHERE status = :status ORDER BY placedAt DESC")
  fun getBetEntriesByStatusFlow(status: String): Flow<List<BetEntry>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBetEntry(betEntry: BetEntry): Long

  @Update
  suspend fun updateBetEntry(betEntry: BetEntry)

  @Query("SELECT * FROM bet_entries WHERE betId = :betId LIMIT 1")
  suspend fun getBetEntryById(betId: String): BetEntry?
}

@Dao
interface MatchDao {
  @Query("SELECT * FROM matches ORDER BY matchDateIso ASC, startTime ASC")
  fun getAllMatchesFlow(): Flow<List<MatchEntity>>

  @Query("SELECT * FROM matches ORDER BY matchDateIso ASC, startTime ASC")
  suspend fun getAllMatches(): List<MatchEntity>

  @Query("SELECT * FROM matches WHERE status = :status ORDER BY matchDateIso ASC, startTime ASC")
  fun getMatchesByStatusFlow(status: String): Flow<List<MatchEntity>>

  @Query("SELECT * FROM matches WHERE status = 'LIVE' ORDER BY minute DESC")
  fun getLiveMatchesFlow(): Flow<List<MatchEntity>>

  @Query("SELECT * FROM matches WHERE status != 'FINISHED' ORDER BY matchDateIso ASC, startTime ASC")
  fun getUpcomingAndLiveMatchesFlow(): Flow<List<MatchEntity>>

  @Query("SELECT * FROM matches WHERE status = 'FINISHED' ORDER BY matchDateIso DESC, startTime DESC")
  fun getFinishedMatchesFlow(): Flow<List<MatchEntity>>

  @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
  suspend fun getMatchById(id: String): MatchEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatches(matches: List<MatchEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatch(match: MatchEntity)

  @Update
  suspend fun updateMatch(match: MatchEntity)

  @Query("UPDATE matches SET isFavorite = :isFav WHERE id = :id")
  suspend fun updateFavorite(id: String, isFav: Boolean)

  @Query("SELECT COUNT(*) FROM matches")
  suspend fun getMatchCount(): Int
}

@Dao
interface LeagueDao {
  @Query("SELECT * FROM leagues")
  fun getAllLeaguesFlow(): Flow<List<LeagueEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLeagues(leagues: List<LeagueEntity>)

  @Query("SELECT COUNT(*) FROM leagues")
  suspend fun getLeagueCount(): Int
}

@Dao
interface OddsDao {
  @Query("SELECT * FROM odds WHERE matchId = :matchId")
  fun getOddsForMatchFlow(matchId: String): Flow<List<OddsEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOdds(odds: List<OddsEntity>)

  @Query("DELETE FROM odds WHERE matchId = :matchId")
  suspend fun deleteOddsForMatch(matchId: String)
}

@Dao
interface UserStatisticsDao {
  @Query("SELECT * FROM user_statistics ORDER BY totalTpWon DESC")
  fun getLeaderboardFlow(): Flow<List<UserStatistics>>

  @Query("SELECT * FROM user_statistics WHERE userId = :userId LIMIT 1")
  fun getUserStatsFlow(userId: String): Flow<UserStatistics?>

  @Query("SELECT * FROM user_statistics WHERE userId = :userId LIMIT 1")
  suspend fun getUserStats(userId: String): UserStatistics?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(stats: UserStatistics)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(statsList: List<UserStatistics>)

  @Query("SELECT COUNT(*) FROM user_statistics")
  suspend fun getCount(): Int
}

@Dao
interface DailyMissionDao {
  @Query("SELECT * FROM daily_missions ORDER BY isClaimed ASC, isCompleted DESC, rewardTp DESC")
  fun getAllMissionsFlow(): Flow<List<DailyMissionEntity>>

  @Query("SELECT * FROM daily_missions WHERE id = :id LIMIT 1")
  suspend fun getMissionById(id: String): DailyMissionEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(missions: List<DailyMissionEntity>)

  @Update
  suspend fun updateMission(mission: DailyMissionEntity)

  @Query("UPDATE daily_missions SET currentProgress = :progress, isCompleted = :completed WHERE id = :id")
  suspend fun updateProgress(id: String, progress: Int, completed: Boolean)

  @Query("UPDATE daily_missions SET isClaimed = 1 WHERE id = :id")
  suspend fun markClaimed(id: String)

  @Query("SELECT COUNT(*) FROM daily_missions")
  suspend fun getCount(): Int
}

