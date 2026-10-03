package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity class 'Match' to store simulated sports event data,
 * including home/away teams, scores, odds, and game status.
 */
@Entity(tableName = "simulated_matches")
data class Match(
  @PrimaryKey val id: String,
  val homeTeam: String,
  val awayTeam: String,
  val homeScore: Int = 0,
  val awayScore: Int = 0,
  val status: String = "SCHEDULED", // SCHEDULED, LIVE, FINISHED
  val homeOdds: Double = 1.0,
  val drawOdds: Double = 1.0,
  val awayOdds: Double = 1.0,
  val over25Odds: Double = 1.85,
  val under25Odds: Double = 1.85,
  val sport: String = "FOOTBALL",
  val league: String = "",
  val minute: Int = 0,
  val startTime: String = "",
  val matchDateIso: String = "",
  val lastUpdated: Long = System.currentTimeMillis()
)
