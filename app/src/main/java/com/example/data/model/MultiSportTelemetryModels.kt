package com.example.data.model

/**
 * Authentic multi-sport detailed telemetry data models.
 * Covers granular metrics for Football, Basketball, Tennis, Motorsports,
 * MMA/Boxing, American Football, Baseball, Snooker, Table Tennis, and Chess.
 */

// 1. Football Telemetry (Gol, Kart, Korner, xG, Şut)
data class FootballTelemetry(
  val goalsHome: Int = 0,
  val goalsAway: Int = 0,
  val yellowCardsHome: Int = 0,
  val yellowCardsAway: Int = 0,
  val redCardsHome: Int = 0,
  val redCardsAway: Int = 0,
  val cornersHome: Int = 0,
  val cornersAway: Int = 0,
  val shotsOnTargetHome: Int = 0,
  val shotsOnTargetAway: Int = 0,
  val possessionHome: Int = 50,
  val xgHome: Double = 0.0,
  val xgAway: Double = 0.0
)

// 2. Basketball Telemetry (Periyot, Sayı, Ribaund, Asist)
data class BasketballTelemetry(
  val period1Home: Int = 0,
  val period1Away: Int = 0,
  val period2Home: Int = 0,
  val period2Away: Int = 0,
  val period3Home: Int = 0,
  val period3Away: Int = 0,
  val period4Home: Int = 0,
  val period4Away: Int = 0,
  val totalPointsHome: Int = 0,
  val totalPointsAway: Int = 0,
  val reboundsHome: Int = 0,
  val reboundsAway: Int = 0,
  val assistsHome: Int = 0,
  val assistsAway: Int = 0,
  val teamFoulsHome: Int = 0,
  val teamFoulsAway: Int = 0
)

// 3. Tennis Telemetry (Set, Game, Tie-Break, Ace, Servis)
data class TennisTelemetry(
  val set1Home: Int = 0,
  val set1Away: Int = 0,
  val set2Home: Int = 0,
  val set2Away: Int = 0,
  val set3Home: Int = 0,
  val set3Away: Int = 0,
  val currentGameScore: String = "40-30",
  val currentServer: String = "home",
  val acesHome: Int = 0,
  val acesAway: Int = 0,
  val doubleFaultsHome: Int = 0,
  val doubleFaultsAway: Int = 0,
  val breakPointsWonHome: Int = 0,
  val breakPointsWonAway: Int = 0
)

// 4. Formula 1 & Motorsports Telemetry (Tur, Pit-stop, Lastik, Sektör Zamanları)
data class MotorsportsTelemetry(
  val currentLap: Int = 1,
  val totalLaps: Int = 58,
  val leaderDriver: String = "Max Verstappen",
  val pitStops: Int = 1,
  val tyreCompound: String = "Medium (M)",
  val tyreLifeLaps: Int = 14,
  val gapToLeader: String = "+2.418s",
  val sector1Time: String = "28.4s",
  val sector2Time: String = "34.1s",
  val sector3Time: String = "22.7s",
  val fastestLapHolder: String = "Lando Norris",
  val fastestLapTime: String = "1:21.450"
)

// 5. Combat Sports Telemetry (MMA, UFC, Boks: Round, Bitiriş Şekli, Vuruşlar)
data class CombatSportsTelemetry(
  val currentRound: Int = 1,
  val totalRounds: Int = 5,
  val roundTimeRemaining: String = "3:42",
  val significantStrikesFighter1: Int = 42,
  val significantStrikesFighter2: Int = 28,
  val takedownsFighter1: Int = 2,
  val takedownsFighter2: Int = 0,
  val controlTimeFighter1: String = "2:15",
  val controlTimeFighter2: String = "0:30",
  val finishMethod: String? = null, // "KO/TKO", "Submission", "Unanimous Decision"
  val referee: String = "Herb Dean"
)

// 6. American Football Telemetry (NFL: Quarter, Down, Yard, Touchdown)
data class AmericanFootballTelemetry(
  val quarter: Int = 1,
  val timeRemaining: String = "08:45",
  val down: Int = 2,
  val yardsToGo: Int = 6,
  val yardLine: String = "SF 34",
  val touchdownsHome: Int = 1,
  val touchdownsAway: Int = 0,
  val fieldGoalsHome: Int = 1,
  val fieldGoalsAway: Int = 1,
  val totalYardsHome: Int = 280,
  val totalYardsAway: Int = 195
)

// 7. Baseball Telemetry (MLB: Inning, Strike, Ball, Out, Runs)
data class BaseballTelemetry(
  val inning: Int = 5,
  val inningHalf: String = "Top",
  val balls: Int = 2,
  val strikes: Int = 1,
  val outs: Int = 1,
  val runsHome: Int = 4,
  val runsAway: Int = 2,
  val hitsHome: Int = 7,
  val hitsAway: Int = 5,
  val errorsHome: Int = 0,
  val errorsAway: Int = 1
)

// 8. Snooker & Darts Telemetry (Frame, Break, 180s, Checkout)
data class SnookerDartsTelemetry(
  val framesWonPlayer1: Int = 4,
  val framesWonPlayer2: Int = 3,
  val currentFrameScore1: Int = 68,
  val currentFrameScore2: Int = 12,
  val highestBreak: Int = 137,
  val oneEightiesCount: Int = 6,
  val checkoutPercentage: Double = 42.5
)

// 9. Table Tennis & Badminton Telemetry (Set, Sayı, Ralli)
data class RacketSportsTelemetry(
  val set1Home: Int = 11,
  val set1Away: Int = 9,
  val set2Home: Int = 8,
  val set2Away: Int = 11,
  val set3Home: Int = 11,
  val set3Away: Int = 7,
  val currentPointsHome: Int = 6,
  val currentPointsAway: Int = 5
)

// 10. Chess Telemetry (Hamle, Süre, Değerlendirme, Açılış)
data class ChessTelemetry(
  val movesPlayed: Int = 34,
  val whitePlayer: String = "Magnus Carlsen",
  val blackPlayer: String = "Hikaru Nakamura",
  val whiteTimeRemaining: String = "12:45",
  val blackTimeRemaining: String = "08:30",
  val evaluation: String = "+1.4 (Beyaz Üstün)",
  val currentOpening: String = "Ruy Lopez - Berlin Savunması"
)
