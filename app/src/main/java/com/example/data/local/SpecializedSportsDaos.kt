package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO interface for Basketball matches (periods, rebounds, quarter scores).
 */
@Dao
interface BasketballMatchDao {

  @Query("SELECT * FROM basketball_matches ORDER BY matchDateIso ASC, startTime ASC")
  fun getAllMatchesFlow(): Flow<List<BasketballMatchEntity>>

  @Query("SELECT * FROM basketball_matches ORDER BY matchDateIso ASC, startTime ASC")
  suspend fun getAllMatches(): List<BasketballMatchEntity>

  @Query("SELECT * FROM basketball_matches WHERE status IN ('LIVE', 'UPCOMING', 'SCHEDULED') ORDER BY status DESC, startTime ASC")
  fun getActiveMatchesFlow(): Flow<List<BasketballMatchEntity>>

  @Query("SELECT * FROM basketball_matches WHERE status = 'LIVE' ORDER BY quarter DESC")
  fun getLiveMatchesFlow(): Flow<List<BasketballMatchEntity>>

  @Query("SELECT * FROM basketball_matches WHERE id = :id LIMIT 1")
  suspend fun getMatchById(id: String): BasketballMatchEntity?

  @Query("SELECT * FROM basketball_matches WHERE homeTeam = :team OR awayTeam = :team ORDER BY matchDateIso DESC LIMIT :limit")
  suspend fun getRecentMatchesForTeam(team: String, limit: Int = 5): List<BasketballMatchEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatches(matches: List<BasketballMatchEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatch(match: BasketballMatchEntity)

  @Update
  suspend fun updateMatch(match: BasketballMatchEntity)

  @Query("DELETE FROM basketball_matches")
  suspend fun clearAll()
}

/**
 * DAO interface for Tennis matches (set-by-set scores, game scores, tie-breaks, aces).
 */
@Dao
interface TennisMatchDao {

  @Query("SELECT * FROM tennis_matches ORDER BY matchDateIso ASC, startTime ASC")
  fun getAllMatchesFlow(): Flow<List<TennisMatchEntity>>

  @Query("SELECT * FROM tennis_matches ORDER BY matchDateIso ASC, startTime ASC")
  suspend fun getAllMatches(): List<TennisMatchEntity>

  @Query("SELECT * FROM tennis_matches WHERE status IN ('LIVE', 'UPCOMING', 'SCHEDULED') ORDER BY status DESC, startTime ASC")
  fun getActiveMatchesFlow(): Flow<List<TennisMatchEntity>>

  @Query("SELECT * FROM tennis_matches WHERE status = 'LIVE' ORDER BY currentSet DESC")
  fun getLiveMatchesFlow(): Flow<List<TennisMatchEntity>>

  @Query("SELECT * FROM tennis_matches WHERE id = :id LIMIT 1")
  suspend fun getMatchById(id: String): TennisMatchEntity?

  @Query("SELECT * FROM tennis_matches WHERE player1 = :player OR player2 = :player ORDER BY matchDateIso DESC LIMIT :limit")
  suspend fun getRecentMatchesForPlayer(player: String, limit: Int = 5): List<TennisMatchEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatches(matches: List<TennisMatchEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMatch(match: TennisMatchEntity)

  @Update
  suspend fun updateMatch(match: TennisMatchEntity)

  @Query("DELETE FROM tennis_matches")
  suspend fun clearAll()
}

/**
 * DAO interface for Formula 1 races (pit-stops, lap telemetry, fastest laps, tyre compounds).
 */
@Dao
interface Formula1RaceDao {

  @Query("SELECT * FROM f1_races ORDER BY raceDateIso ASC, startTime ASC")
  fun getAllRacesFlow(): Flow<List<Formula1RaceEntity>>

  @Query("SELECT * FROM f1_races ORDER BY raceDateIso ASC, startTime ASC")
  suspend fun getAllRaces(): List<Formula1RaceEntity>

  @Query("SELECT * FROM f1_races WHERE status IN ('LIVE', 'UPCOMING', 'SCHEDULED') ORDER BY status DESC")
  fun getActiveRacesFlow(): Flow<List<Formula1RaceEntity>>

  @Query("SELECT * FROM f1_races WHERE status = 'LIVE' ORDER BY currentLap DESC")
  fun getLiveRacesFlow(): Flow<List<Formula1RaceEntity>>

  @Query("SELECT * FROM f1_races WHERE id = :id LIMIT 1")
  suspend fun getRaceById(id: String): Formula1RaceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRaces(races: List<Formula1RaceEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRace(race: Formula1RaceEntity)

  @Update
  suspend fun updateRace(race: Formula1RaceEntity)

  @Query("DELETE FROM f1_races")
  suspend fun clearAll()
}
