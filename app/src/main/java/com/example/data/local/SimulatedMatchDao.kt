package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulatedMatchDao {
  @Query("SELECT * FROM simulated_matches ORDER BY matchDateIso ASC, startTime ASC")
  fun getAllMatchesFlow(): Flow<List<Match>>

  @Query("SELECT * FROM simulated_matches ORDER BY matchDateIso ASC, startTime ASC")
  suspend fun getAllMatches(): List<Match>

  @Query("SELECT * FROM simulated_matches WHERE status = :status")
  fun getMatchesByStatusFlow(status: String): Flow<List<Match>>

  @Query("SELECT * FROM simulated_matches WHERE id = :id LIMIT 1")
  suspend fun getMatchById(id: String): Match?

  @Query("SELECT * FROM simulated_matches WHERE status IN ('LIVE', 'UPCOMING', 'SCHEDULED') ORDER BY status DESC, startTime ASC")
  fun getActiveMatchesFlow(): Flow<List<Match>>

  @Query("SELECT * FROM simulated_matches WHERE status IN ('LIVE', 'UPCOMING', 'SCHEDULED')")
  suspend fun getActiveMatches(): List<Match>

  @Query("SELECT * FROM simulated_matches WHERE status = 'LIVE' ORDER BY minute DESC")
  fun getLiveMatchesFlow(): Flow<List<Match>>

  @Query("SELECT * FROM simulated_matches WHERE status IN ('UPCOMING', 'SCHEDULED') ORDER BY startTime ASC")
  fun getUpcomingMatchesFlow(): Flow<List<Match>>

  @Query("SELECT * FROM simulated_matches WHERE id = :id AND status != 'FINISHED' LIMIT 1")
  suspend fun getActiveMatchById(id: String): Match?

  @Query("SELECT * FROM simulated_matches WHERE sport = :sport ORDER BY startTime ASC")
  fun getMatchesBySportFlow(sport: String): Flow<List<Match>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatches(matches: List<Match>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatch(match: Match)

  @Update
  suspend fun updateMatch(match: Match)

  @Query("DELETE FROM simulated_matches")
  suspend fun clearAll()
}
