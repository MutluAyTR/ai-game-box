package com.example.data.model

data class PlayerLineup(
  val number: Int,
  val name: String,
  val position: String, // GK, DEF, MID, FWD
  val rating: Double,
  val isCaptain: Boolean = false,
  val yellowCards: Int = 0,
  val redCards: Int = 0,
  val goals: Int = 0,
  val marketValue: String = "€1.5M"
)

data class TeamLineup(
  val formation: String, // e.g., "4-2-3-1", "5-3-2", "4-3-3"
  val coach: String,
  val starters: List<PlayerLineup>,
  val substitutes: List<PlayerLineup>
)

data class MatchLineups(
  val home: TeamLineup,
  val away: TeamLineup
)

data class MatchH2H(
  val totalPlayed: Int,
  val homeWins: Int,
  val draws: Int,
  val awayWins: Int,
  val recentMatches: List<H2HMatch>
)

data class H2HMatch(
  val date: String,
  val league: String,
  val score: String,
  val result: String // G, B, M
)

data class LeagueStandingRow(
  val rank: Int,
  val teamName: String,
  val played: Int,
  val won: Int,
  val drawn: Int,
  val lost: Int,
  val goalsFor: Int,
  val goalsAgainst: Int,
  val goalDifference: Int,
  val points: Int,
  val form: List<String> = listOf("G", "G", "B", "G", "M"),
  val zoneColor: Long = 0xFF10B981,
  val isBasketball: Boolean = false
)

data class DroppingOddItem(
  val matchId: String,
  val homeTeam: String,
  val awayTeam: String,
  val league: String,
  val marketName: String,
  val initialOdd: Double,
  val currentOdd: Double,
  val dropPercentage: Int,
  val reason: String,
  val sport: Sport = Sport.FOOTBALL
)

data class PopularBetItem(
  val matchId: String,
  val homeTeam: String,
  val awayTeam: String,
  val league: String,
  val selectionName: String,
  val odd: Double,
  val percentagePlayed: Int,
  val totalBetsPlaced: Int,
  val sport: Sport = Sport.FOOTBALL
)
