package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Specialized Room database entity for Basketball events.
 * Stores granular period-by-period scores, rebounds (offensive/defensive/total),
 * assists, fouls, and basketball-specific betting lines.
 */
@Entity(tableName = "basketball_matches")
data class BasketballMatchEntity(
  @PrimaryKey val id: String,
  val league: String, // e.g. "NBA", "EuroLeague", "BSL"
  val homeTeam: String,
  val awayTeam: String,
  val status: String = "SCHEDULED", // SCHEDULED, LIVE, UPCOMING, FINISHED
  val totalHomeScore: Int = 0,
  val totalAwayScore: Int = 0,
  val quarter: Int = 1, // 1 to 4, or 5 for OT
  val timeRemaining: String = "10:00",
  // Period Scores (Çeyrek Skorları)
  val q1Home: Int = 0,
  val q1Away: Int = 0,
  val q2Home: Int = 0,
  val q2Away: Int = 0,
  val q3Home: Int = 0,
  val q3Away: Int = 0,
  val q4Home: Int = 0,
  val q4Away: Int = 0,
  val otHome: Int = 0,
  val otAway: Int = 0,
  // Rebounds & Detailed Stats (Ribaund & İstatistikler)
  val offensiveReboundsHome: Int = 0,
  val offensiveReboundsAway: Int = 0,
  val defensiveReboundsHome: Int = 0,
  val defensiveReboundsAway: Int = 0,
  val totalReboundsHome: Int = 0,
  val totalReboundsAway: Int = 0,
  val assistsHome: Int = 0,
  val assistsAway: Int = 0,
  val teamFoulsHome: Int = 0,
  val teamFoulsAway: Int = 0,
  // Betting Odds & Spreads
  val oddsHomeWin: Double = 1.80,
  val oddsAwayWin: Double = 1.95,
  val handicapHome: Double = -3.5,
  val handicapOdds: Double = 1.90,
  val totalOverUnderThreshold: Double = 166.5,
  val overOdds: Double = 1.85,
  val underOdds: Double = 1.85,
  val startTime: String = "",
  val matchDateIso: String = "",
  val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Specialized Room database entity for Tennis events.
 * Stores individual set scores, current game scores (e.g. 40-30, Deuce),
 * tie-break details, aces, and serve percentages.
 */
@Entity(tableName = "tennis_matches")
data class TennisMatchEntity(
  @PrimaryKey val id: String,
  val tournament: String, // e.g. "ATP Finals", "Wimbledon", "US Open"
  val courtSurface: String = "Hard", // Hard, Clay, Grass
  val round: String = "Çeyrek Final",
  val player1: String,
  val player2: String,
  val status: String = "SCHEDULED", // SCHEDULED, LIVE, UPCOMING, FINISHED
  val setsWonPlayer1: Int = 0,
  val setsWonPlayer2: Int = 0,
  val currentSet: Int = 1,
  val servingPlayer: String = "player1", // "player1" or "player2"
  // Set-by-Set Scores
  val set1Player1: Int = 0,
  val set1Player2: Int = 0,
  val set2Player1: Int = 0,
  val set2Player2: Int = 0,
  val set3Player1: Int = 0,
  val set3Player2: Int = 0,
  val set4Player1: Int = 0,
  val set4Player2: Int = 0,
  val set5Player1: Int = 0,
  val set5Player2: Int = 0,
  // Current Game & Tie-Breaks
  val currentGameScore1: String = "0", // 0, 15, 30, 40, AD
  val currentGameScore2: String = "0",
  val tieBreakSet1: String = "",
  val tieBreakSet2: String = "",
  // Service & Ace Telemetry
  val acesPlayer1: Int = 0,
  val acesPlayer2: Int = 0,
  val doubleFaultsPlayer1: Int = 0,
  val doubleFaultsPlayer2: Int = 0,
  val breakPointsWonPlayer1: Int = 0,
  val breakPointsWonPlayer2: Int = 0,
  val firstServePercentagePlayer1: Int = 65,
  val firstServePercentagePlayer2: Int = 62,
  // Betting Odds
  val oddsPlayer1: Double = 1.75,
  val oddsPlayer2: Double = 2.05,
  val totalGamesThreshold: Double = 22.5,
  val overGamesOdds: Double = 1.85,
  val underGamesOdds: Double = 1.85,
  val startTime: String = "",
  val matchDateIso: String = "",
  val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Specialized Room database entity for Formula 1 & Motorsports events.
 * Stores pit-stop counts, lap-by-lap telemetry, sector times, tyre compounds,
 * safety car statuses, and race winner odds.
 */
@Entity(tableName = "f1_races")
data class Formula1RaceEntity(
  @PrimaryKey val id: String,
  val grandPrixName: String, // e.g. "Türkiye GP 2026", "Monaco Grand Prix"
  val circuitName: String,
  val country: String,
  val status: String = "SCHEDULED", // SCHEDULED, LIVE, UPCOMING, FINISHED
  val raceDateIso: String = "",
  val startTime: String = "",
  // Lap & Sector Telemetry
  val currentLap: Int = 1,
  val totalLaps: Int = 58,
  val leaderDriver: String = "Max Verstappen",
  val secondDriver: String = "Lando Norris",
  val thirdDriver: String = "Charles Leclerc",
  val gapToLeaderSeconds: String = "+1.842s",
  val fastestLapDriver: String = "Lando Norris",
  val fastestLapTime: String = "1:21.845",
  val sector1Time: String = "28.3s",
  val sector2Time: String = "34.2s",
  val sector3Time: String = "22.6s",
  // Pit-Stops & Tyres
  val leaderPitStops: Int = 1,
  val secondPitStops: Int = 1,
  val lastPitStopLap: Int = 24,
  val leaderTyreCompound: String = "MEDIUM", // SOFT, MEDIUM, HARD, INTERMEDIATE, WET
  val tyreLapsAge: Int = 18,
  val trackStatus: String = "GREEN_FLAG", // GREEN_FLAG, YELLOW_FLAG, SAFETY_CAR, VSC, RED_FLAG
  // Odds
  val oddsLeaderWin: Double = 1.65,
  val oddsSecondWin: Double = 2.40,
  val oddsThirdWin: Double = 4.50,
  val oddsFastestLap: Double = 2.10,
  val lastUpdated: Long = System.currentTimeMillis()
)
