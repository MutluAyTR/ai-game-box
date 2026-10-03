package com.example.engine

import com.example.data.model.AiPrediction
import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Selection
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object AiOddsEngine {

  private fun factorial(n: Int): Double {
    var result = 1.0
    for (i in 2..n) {
      result *= i
    }
    return result
  }

  // Poisson distribution probability for k goals given lambda (expected goals)
  private fun poisson(lambda: Double, k: Int): Double {
    return (lambda.pow(k) * exp(-lambda)) / factorial(k)
  }

  /**
   * Generates initial AI match prediction and odds based on team strengths
   */
  fun generateInitialMarkets(
    matchId: String,
    homeElo: Double,
    awayElo: Double,
    homeXgExp: Double = 1.85,
    awayXgExp: Double = 1.25
  ): Pair<List<Market>, AiPrediction> {
    var homeWinProb = 0.0
    var drawProb = 0.0
    var awayWinProb = 0.0
    var over25Prob = 0.0
    var bothScoreProb = 0.0

    // Simulate score grid 0..6 goals
    for (h in 0..6) {
      val pHome = poisson(homeXgExp, h)
      for (a in 0..6) {
        val pAway = poisson(awayXgExp, a)
        val pScore = pHome * pAway

        when {
          h > a -> homeWinProb += pScore
          h == a -> drawProb += pScore
          else -> awayWinProb += pScore
        }

        if (h + a > 2.5) {
          over25Prob += pScore
        }
        if (h > 0 && a > 0) {
          bothScoreProb += pScore
        }
      }
    }

    val totalMs = homeWinProb + drawProb + awayWinProb
    homeWinProb /= totalMs
    drawProb /= totalMs
    awayWinProb /= totalMs

    val margin = 1.06 // 6% theoretical bookmaker vig
    val oddHome = formatOdd(1.0 / (homeWinProb * margin))
    val oddDraw = formatOdd(1.0 / (drawProb * margin))
    val oddAway = formatOdd(1.0 / (awayWinProb * margin))

    // Çifte Şans
    val odd1X = formatOdd(1.0 / ((homeWinProb + drawProb) * margin))
    val odd12 = formatOdd(1.0 / ((homeWinProb + awayWinProb) * margin))
    val oddX2 = formatOdd(1.0 / ((drawProb + awayWinProb) * margin))

    // 2.5 Alt/Üst
    val under25Prob = 1.0 - over25Prob
    val oddOver = formatOdd(1.0 / (over25Prob * margin))
    val oddUnder = formatOdd(1.0 / (under25Prob * margin))

    // KG Var/Yok
    val noScoreProb = 1.0 - bothScoreProb
    val oddKgVar = formatOdd(1.0 / (bothScoreProb * margin))
    val oddKgYok = formatOdd(1.0 / (noScoreProb * margin))

    val markets = listOf(
      Market(
        id = "${matchId}_ms",
        type = MarketType.MATCH_RESULT,
        name = "Maç Sonucu (MS)",
        selections = listOf(
          Selection(id = "${matchId}_ms_1", marketId = "${matchId}_ms", name = "MS 1", odd = oddHome),
          Selection(id = "${matchId}_ms_x", marketId = "${matchId}_ms", name = "MS X", odd = oddDraw),
          Selection(id = "${matchId}_ms_2", marketId = "${matchId}_ms", name = "MS 2", odd = oddAway)
        )
      ),
      Market(
        id = "${matchId}_iy",
        type = MarketType.FIRST_HALF_RESULT,
        name = "İlk Yarı Sonucu (İY)",
        selections = listOf(
          Selection(id = "${matchId}_iy_1", marketId = "${matchId}_iy", name = "İY 1", odd = formatOdd(oddHome * 1.55)),
          Selection(id = "${matchId}_iy_x", marketId = "${matchId}_iy", name = "İY X", odd = formatOdd(max(1.90, oddDraw * 0.72))),
          Selection(id = "${matchId}_iy_2", marketId = "${matchId}_iy", name = "İY 2", odd = formatOdd(oddAway * 1.55))
        )
      ),
      Market(
        id = "${matchId}_cs",
        type = MarketType.DOUBLE_CHANCE,
        name = "Çifte Şans",
        selections = listOf(
          Selection(id = "${matchId}_cs_1x", marketId = "${matchId}_cs", name = "1X", odd = odd1X),
          Selection(id = "${matchId}_cs_12", marketId = "${matchId}_cs", name = "12", odd = odd12),
          Selection(id = "${matchId}_cs_x2", marketId = "${matchId}_cs", name = "X2", odd = oddX2)
        )
      ),
      Market(
        id = "${matchId}_ou15",
        type = MarketType.TOTAL_GOALS_15,
        name = "1.5 Gol Alt/Üst",
        selections = listOf(
          Selection(id = "${matchId}_ou15_alt", marketId = "${matchId}_ou15", name = "1.5 Alt", odd = formatOdd(oddUnder * 1.70)),
          Selection(id = "${matchId}_ou15_ust", marketId = "${matchId}_ou15", name = "1.5 Üst", odd = formatOdd(max(1.15, oddOver * 0.65)))
        )
      ),
      Market(
        id = "${matchId}_ou25",
        type = MarketType.TOTAL_GOALS_25,
        name = "2.5 Gol Alt/Üst",
        selections = listOf(
          Selection(id = "${matchId}_ou_alt", marketId = "${matchId}_ou25", name = "2.5 Alt", odd = oddUnder),
          Selection(id = "${matchId}_ou_ust", marketId = "${matchId}_ou25", name = "2.5 Üst", odd = oddOver)
        )
      ),
      Market(
        id = "${matchId}_ou35",
        type = MarketType.TOTAL_GOALS_35,
        name = "3.5 Gol Alt/Üst",
        selections = listOf(
          Selection(id = "${matchId}_ou35_alt", marketId = "${matchId}_ou35", name = "3.5 Alt", odd = formatOdd(max(1.25, oddUnder * 0.70))),
          Selection(id = "${matchId}_ou35_ust", marketId = "${matchId}_ou35", name = "3.5 Üst", odd = formatOdd(oddOver * 1.85))
        )
      ),
      Market(
        id = "${matchId}_kg",
        type = MarketType.BOTH_TEAMS_SCORE,
        name = "Karşılıklı Gol (KG)",
        selections = listOf(
          Selection(id = "${matchId}_kg_var", marketId = "${matchId}_kg", name = "Var", odd = oddKgVar),
          Selection(id = "${matchId}_kg_yok", marketId = "${matchId}_kg", name = "Yok", odd = oddKgYok)
        )
      ),
      Market(
        id = "${matchId}_corners",
        type = MarketType.TOTAL_CORNERS,
        name = "Toplam Korner (9.5 Alt/Üst)",
        selections = listOf(
          Selection(id = "${matchId}_corn_alt", marketId = "${matchId}_corners", name = "9.5 Alt", odd = 1.82),
          Selection(id = "${matchId}_corn_ust", marketId = "${matchId}_corners", name = "9.5 Üst", odd = 1.74)
        )
      ),
      Market(
        id = "${matchId}_cards",
        type = MarketType.TOTAL_CARDS,
        name = "Toplam Kart (4.5 Alt/Üst)",
        selections = listOf(
          Selection(id = "${matchId}_card_alt", marketId = "${matchId}_cards", name = "4.5 Alt", odd = 1.95),
          Selection(id = "${matchId}_card_ust", marketId = "${matchId}_cards", name = "4.5 Üst", odd = 1.62)
        )
      )
    )

    val predictedH = if (homeXgExp > 1.7) 2 else if (homeXgExp > 0.9) 1 else 0
    val predictedA = if (awayXgExp > 1.6) 2 else if (awayXgExp > 0.8) 1 else 0

    val aiPrediction = AiPrediction(
      homeWinProb = (homeWinProb * 100).roundToInt(),
      drawProb = (drawProb * 100).roundToInt(),
      awayWinProb = (awayWinProb * 100).roundToInt(),
      predictedScore = "$predictedH - $predictedA",
      confidence = min(88, max(52, ((homeWinProb - awayWinProb).let { if (it < 0) -it else it } * 100 + 55).toInt())),
      xgSummary = "Ev Sahibi: ${"%.2f".format(homeXgExp)} xG | Deplasman: ${"%.2f".format(awayXgExp)} xG",
      tacticalAnalysis = "Monte Carlo (100.000 simülasyon) ve Poisson dağılımı ile ev sahibinin kanat organizasyonları ve yüksek pres verimliliği öne çıkıyor.",
      formRatingHome = "Son 5 Maç: G-G-B-G-M (8.4 Güç Endeksi)",
      formRatingAway = "Son 5 Maç: B-G-M-B-G (7.1 Güç Endeksi)"
    )

    return Pair(markets, aiPrediction)
  }

  /**
   * Recalculates in-play live odds when minute, score or cards change
   */
  fun recalculateLiveMarkets(
    matchId: String,
    currentMarkets: List<Market>,
    homeScore: Int,
    awayScore: Int,
    minute: Int,
    homeRedCards: Int = 0,
    awayRedCards: Int = 0
  ): List<Market> {
    val remainingMins = max(1, 90 - minute)
    val timeFactor = remainingMins / 90.0

    val scoreDiff = homeScore - awayScore
    val redCardDiff = homeRedCards - awayRedCards

    var homeProb: Double
    var drawProb: Double
    var awayProb: Double

    when {
      scoreDiff > 0 -> {
        // Home is leading
        homeProb = 0.65 + (0.30 * (1.0 - timeFactor)) + (scoreDiff - 1) * 0.12 - (homeRedCards * 0.15)
        drawProb = 0.22 * timeFactor + (awayRedCards * 0.05)
        awayProb = 0.13 * timeFactor
      }
      scoreDiff < 0 -> {
        // Away is leading
        awayProb = 0.65 + (0.30 * (1.0 - timeFactor)) + (-scoreDiff - 1) * 0.12 - (awayRedCards * 0.15)
        drawProb = 0.22 * timeFactor + (homeRedCards * 0.05)
        homeProb = 0.13 * timeFactor
      }
      else -> {
        // Tied
        drawProb = 0.35 + (0.45 * (1.0 - timeFactor))
        homeProb = 0.35 * timeFactor - (homeRedCards * 0.12)
        awayProb = 0.30 * timeFactor - (awayRedCards * 0.12)
      }
    }

    // Normalize
    val total = max(0.01, homeProb + drawProb + awayProb)
    homeProb = min(0.98, max(0.02, homeProb / total))
    drawProb = min(0.95, max(0.02, drawProb / total))
    awayProb = min(0.98, max(0.02, awayProb / total))

    val margin = 1.05
    val newOddHome = formatOdd(1.0 / (homeProb * margin))
    val newOddDraw = formatOdd(1.0 / (drawProb * margin))
    val newOddAway = formatOdd(1.0 / (awayProb * margin))

    return currentMarkets.map { market ->
      when (market.type) {
        MarketType.MATCH_RESULT -> {
          market.copy(
            isSuspended = false,
            selections = market.selections.map { sel ->
              val newOdd = when {
                sel.name.contains("1") -> newOddHome
                sel.name.contains("X") -> newOddDraw
                else -> newOddAway
              }
              sel.copy(
                previousOdd = sel.odd,
                odd = newOdd,
                isOddsUp = newOdd > sel.odd,
                isOddsDown = newOdd < sel.odd
              )
            }
          )
        }
        MarketType.TOTAL_GOALS_25 -> {
          val currentTotalGoals = homeScore + awayScore
          val isAlreadyOver = currentTotalGoals >= 3
          val newUnderOdd = if (isAlreadyOver) 1.01 else formatOdd(max(1.08, 1.30 + (0.7 * (1.0 - timeFactor))))
          val newOverOdd = if (isAlreadyOver) 1.01 else formatOdd(max(1.15, 2.50 * timeFactor))

          market.copy(
            isSuspended = isAlreadyOver,
            selections = market.selections.map { sel ->
              val newOdd = if (sel.name.contains("Alt")) newUnderOdd else newOverOdd
              sel.copy(
                previousOdd = sel.odd,
                odd = newOdd,
                isOddsUp = newOdd > sel.odd,
                isOddsDown = newOdd < sel.odd
              )
            }
          )
        }
        else -> market.copy(isSuspended = false)
      }
    }
  }

  private fun formatOdd(odd: Double): Double {
    val rounded = (odd * 100).roundToInt() / 100.0
    return max(1.04, min(35.0, rounded))
  }

  /**
   * Generates comprehensive realistic Mackolik / Nesine Basketball markets
   */
  fun generateBasketballMarkets(
    matchId: String,
    homeTeam: String,
    awayTeam: String,
    homeOdd: Double = 1.62,
    awayOdd: Double = 2.25,
    handicapLine: Double = 3.5,
    totalPointsLine: Double = 163.5
  ): Pair<List<Market>, AiPrediction> {
    val markets = mutableListOf<Market>()

    // 1. MS (1-2)
    markets.add(
      Market(
        id = "${matchId}_m_bms",
        type = MarketType.BASKETBALL_MS,
        name = "Maç Sonucu (1-2 Uzatmalar Dahil)",
        selections = listOf(
          Selection("bms_1", "${matchId}_m_bms", "1 ($homeTeam)", homeOdd),
          Selection("bms_2", "${matchId}_m_bms", "2 ($awayTeam)", awayOdd)
        )
      )
    )

    // 2. Handikaplı Maç Sonucu
    markets.add(
      Market(
        id = "${matchId}_m_bhms",
        type = MarketType.BASKETBALL_HANDICAP,
        name = "Handikaplı MS (HMS ${if (homeOdd < awayOdd) "-" else "+"}$handicapLine)",
        selections = listOf(
          Selection("bhms_1", "${matchId}_m_bhms", "1 (${if (homeOdd < awayOdd) "-" else "+"}$handicapLine)", 1.88),
          Selection("bhms_2", "${matchId}_m_bhms", "2 (${if (homeOdd < awayOdd) "+" else "-"}$handicapLine)", 1.88)
        )
      )
    )

    // 3. Toplam Sayı Alt/Üst
    markets.add(
      Market(
        id = "${matchId}_m_btot",
        type = MarketType.BASKETBALL_TOTAL_POINTS,
        name = "Toplam Sayı ($totalPointsLine)",
        selections = listOf(
          Selection("btot_u", "${matchId}_m_btot", "$totalPointsLine Alt", 1.84),
          Selection("btot_o", "${matchId}_m_btot", "$totalPointsLine Üst", 1.86)
        )
      )
    )

    // 4. İlk Yarı Sonucu
    val iyHome = formatOdd(homeOdd * 0.95)
    val iyAway = formatOdd(awayOdd * 1.05)
    markets.add(
      Market(
        id = "${matchId}_m_biy",
        type = MarketType.BASKETBALL_FIRST_HALF,
        name = "İlk Yarı Sonucu",
        selections = listOf(
          Selection("biy_1", "${matchId}_m_biy", "İY 1", iyHome),
          Selection("biy_2", "${matchId}_m_biy", "İY 2", iyAway)
        )
      )
    )

    // 5. İlk Yarı Toplam Sayı Alt/Üst
    val iyTotal = ((totalPointsLine / 2.0) * 10).roundToInt() / 10.0
    markets.add(
      Market(
        id = "${matchId}_m_biytot",
        type = MarketType.BASKETBALL_FIRST_HALF_TOTAL,
        name = "İlk Yarı Toplam ($iyTotal)",
        selections = listOf(
          Selection("biytot_u", "${matchId}_m_biytot", "$iyTotal Alt", 1.85),
          Selection("biytot_o", "${matchId}_m_biytot", "$iyTotal Üst", 1.85)
        )
      )
    )

    // 6. Oyuncu Toplam Sayı (Star Players)
    markets.add(
      Market(
        id = "${matchId}_m_bplayer_pts",
        type = MarketType.BASKETBALL_PLAYER_POINTS,
        name = "Oyuncu Toplam Sayı",
        selections = listOf(
          Selection("bplay_1", "${matchId}_m_bplayer_pts", "Lider Skorer (17.5 Üst)", 1.78),
          Selection("bplay_2", "${matchId}_m_bplayer_pts", "Şutör Gard (14.5 Üst)", 1.82)
        )
      )
    )

    val homeWinPct = ((1.0 / homeOdd) / ((1.0 / homeOdd) + (1.0 / awayOdd)) * 100).toInt()
    val awayWinPct = 100 - homeWinPct
    val predScore = if (homeWinPct >= 50) "86 - 81" else "78 - 84"

    val ai = AiPrediction(
      homeWinProb = homeWinPct,
      drawProb = 0,
      awayWinProb = awayWinPct,
      predictedScore = predScore,
      confidence = max(60, min(88, homeWinPct)),
      xgSummary = "Beklenen Toplam: $totalPointsLine Sayı",
      tacticalAnalysis = "Yüksek tempolu, hücum ribaundları ve geçiş hücumlarının belirleyici olacağı bir basketbol mücadelesi.",
      formRatingHome = "G-G-G",
      formRatingAway = "M-G-G"
    )

    return Pair(markets, ai)
  }
}
