package com.example.data.model

/**
 * Historical AI Betting Prediction model for tracking the accuracy and performance
 * of the Gemini AI sports model's advice.
 */
data class HistoricalAiPrediction(
  val id: String,
  val matchTitle: String,
  val league: String,
  val sport: Sport,
  val matchDate: String,
  val predictedTip: String,
  val predictedOdds: Double,
  val actualOutcomeScore: String,
  val isWon: Boolean,
  val confidence: Int,
  val geminiModel: String = "Gemini 2.5 Flash",
  val analysisSummary: String,
  val stakeTp: Long = 100L,
  val returnTp: Long = if (isWon) (100L * predictedOdds).toLong() else 0L
)

/**
 * Team Form Data Point for Recharts / D3 equivalent line/area chart.
 */
data class TeamFormPoint(
  val matchNumber: Int,
  val opponent: String,
  val isHome: Boolean,
  val result: String, // "W", "D", "L" (G, B, M)
  val points: Int,    // 3, 1, 0
  val goalsFor: Int,
  val goalsAgainst: Int,
  val xg: Double,
  val score: String
)

/**
 * League Standing Item for visual standings bar chart.
 */
data class VisualStandingItem(
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
  val formLast5: List<String>, // ["W", "W", "D", "W", "L"]
  val isUserFavorite: Boolean = false
)
