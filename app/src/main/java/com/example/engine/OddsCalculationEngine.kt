package com.example.engine

import com.example.data.model.Market
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.Selection
import com.example.data.model.Sport
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Ultra-realistic dynamic in-play odds calculation and market suspension engine.
 *
 * Implements:
 * 1. 85th minute rule: When minute >= 85, all in-play markets are strictly locked/suspended.
 * 2. Realistic leading odds: A team leading 2-1 never receives ~2.00 odds. In-play odds for
 *    the leading team range between 1.05 - 1.25 depending on the minute and goal difference.
 * 3. Under/Over threshold closures: If score is 2-1 or 4-1 (total >= 3), 2.5 Alt is strictly KAPALI
 *    (closed/suspended), because the outcome is already lost.
 * 4. Both Teams to Score closure: If both teams scored (e.g. 1-1, 2-1, 4-1), KG market is closed/settled.
 */
object OddsCalculationEngine {

  /**
   * Recalculates all in-play markets for a live match based on real-time score, minute, and sport.
   */
  fun recalculateLiveMarkets(
    match: Match,
    homeScore: Int,
    awayScore: Int,
    minute: Int,
    isVarCheck: Boolean = false
  ): List<Market> {
    // 1. 85th Minute Closure Rule: At or beyond 85th minute, all betting is locked/closed.
    val isAfter85 = (match.sport == Sport.FOOTBALL && minute >= 85) ||
        (match.sport == Sport.BASKETBALL && minute >= 38) ||
        (match.sport == Sport.TENNIS && minute >= 130)

    val globalSuspended = isVarCheck || isAfter85

    val goalDiff = homeScore - awayScore
    val totalGoals = homeScore + awayScore
    val remainingMinutes = max(1, 90 - minute)

    return match.markets.map { market ->
      when (market.type) {
        MarketType.MATCH_RESULT -> {
          calculateMatchResult(market, goalDiff, minute, remainingMinutes, globalSuspended)
        }
        MarketType.TOTAL_GOALS_25 -> {
          // If total goals >= 3, 2.5 Alt has already lost! Market must be KAPALI (suspended).
          val isGoalCapBusted = totalGoals >= 3
          calculateOverUnder(
            market = market,
            threshold = 2.5,
            totalGoals = totalGoals,
            remainingMinutes = remainingMinutes,
            isSuspended = globalSuspended || isGoalCapBusted
          )
        }
        MarketType.TOTAL_GOALS_15 -> {
          val isGoalCapBusted = totalGoals >= 2
          calculateOverUnder(
            market = market,
            threshold = 1.5,
            totalGoals = totalGoals,
            remainingMinutes = remainingMinutes,
            isSuspended = globalSuspended || isGoalCapBusted
          )
        }
        MarketType.TOTAL_GOALS_35 -> {
          val isGoalCapBusted = totalGoals >= 4
          calculateOverUnder(
            market = market,
            threshold = 3.5,
            totalGoals = totalGoals,
            remainingMinutes = remainingMinutes,
            isSuspended = globalSuspended || isGoalCapBusted
          )
        }
        MarketType.BOTH_TEAMS_SCORE -> {
          // If both teams have scored, KG VAR is achieved and settled! Market is closed.
          val bothScored = homeScore >= 1 && awayScore >= 1
          calculateBothTeamsScore(market, homeScore, awayScore, remainingMinutes, globalSuspended || bothScored)
        }
        MarketType.DOUBLE_CHANCE -> {
          calculateDoubleChance(market, goalDiff, minute, remainingMinutes, globalSuspended)
        }
        MarketType.FIRST_HALF_RESULT -> {
          val isSecondHalf = minute > 45
          market.copy(
            isSuspended = globalSuspended || isSecondHalf,
            selections = market.selections.map { sel ->
              sel.copy(previousOdd = sel.odd)
            }
          )
        }
        MarketType.HANDICAP_RESULT -> {
          calculateHandicap(market, goalDiff, minute, globalSuspended)
        }
        MarketType.PLAYER_ANYTIME_GOAL,
        MarketType.PLAYER_FIRST_GOAL,
        MarketType.PLAYER_SHOT_ON_TARGET -> {
          market.copy(
            isSuspended = globalSuspended,
            selections = market.selections.map { sel ->
              // Player odds slightly increase as remaining time decreases
              val decayFactor = 1.0 + (minute.toDouble() / 150.0)
              val newOdd = roundOdd(sel.odd * decayFactor)
              sel.copy(
                odd = newOdd,
                previousOdd = sel.odd,
                isOddsUp = newOdd > sel.odd,
                isOddsDown = newOdd < sel.odd
              )
            }
          )
        }
        MarketType.BASKETBALL_MS -> {
          val diff = homeScore - awayScore
          val (homeOdd, awayOdd) = when {
            diff >= 15 -> Pair(1.03, 10.00)
            diff >= 10 -> Pair(1.10, 6.50)
            diff >= 6 -> Pair(1.25, 3.80)
            diff >= 3 -> Pair(1.45, 2.70)
            diff in -2..2 -> Pair(1.88, 1.88)
            diff <= -15 -> Pair(10.00, 1.03)
            diff <= -10 -> Pair(6.50, 1.10)
            diff <= -6 -> Pair(3.80, 1.25)
            else -> Pair(2.70, 1.45)
          }
          val updatedSelections = market.selections.map { sel ->
            val newOdd = if (sel.name.startsWith("1") || sel.id.contains("1")) homeOdd else awayOdd
            val valid = roundOdd(max(1.02, newOdd))
            sel.copy(
              odd = valid,
              previousOdd = sel.odd,
              isOddsUp = valid > sel.odd,
              isOddsDown = valid < sel.odd
            )
          }
          market.copy(selections = updatedSelections, isSuspended = globalSuspended)
        }
        MarketType.BASKETBALL_TOTAL_POINTS -> {
          val currentTotal = homeScore + awayScore
          val updatedSelections = market.selections.map { sel ->
            val isAlt = sel.name.contains("Alt", ignoreCase = true)
            // As points accumulate, dynamically adjust odds
            val baseOdd = if (isAlt) 1.85 else 1.85
            val valid = roundOdd(baseOdd)
            sel.copy(odd = valid, previousOdd = sel.odd)
          }
          market.copy(selections = updatedSelections, isSuspended = globalSuspended)
        }
        else -> {
          market.copy(isSuspended = globalSuspended)
        }
      }
    }
  }

  private fun calculateMatchResult(
    market: Market,
    goalDiff: Int,
    minute: Int,
    remainingMinutes: Int,
    isSuspended: Boolean
  ): Market {
    val (rawHome, rawDraw, rawAway) = when {
      // Home leading by 1 goal (e.g. 1-0 or 2-1)
      goalDiff == 1 -> {
        when {
          minute < 30 -> Triple(1.45, 3.80, 6.50)
          minute < 60 -> Triple(1.30, 4.50, 9.00)
          minute < 75 -> Triple(1.18, 5.50, 14.00) // At 65-75 min leading 2-1: 1.15-1.20
          else -> Triple(1.08, 7.50, 22.00)        // At 80 min leading 2-1: 1.05-1.10
        }
      }
      // Home leading by 2+ goals (e.g. 2-0, 3-1, 4-1)
      goalDiff >= 2 -> {
        when {
          minute < 45 -> Triple(1.10, 8.50, 20.00)
          minute < 70 -> Triple(1.04, 15.00, 35.00)
          else -> Triple(1.02, 25.00, 60.00)       // 4-1 in second half: 1.02
        }
      }
      // Away leading by 1 goal (e.g. 0-1 or 1-2)
      goalDiff == -1 -> {
        when {
          minute < 30 -> Triple(6.50, 3.80, 1.45)
          minute < 60 -> Triple(9.00, 4.50, 1.30)
          minute < 75 -> Triple(14.00, 5.50, 1.18)
          else -> Triple(22.00, 7.50, 1.08)
        }
      }
      // Away leading by 2+ goals (e.g. 0-2, 1-3, 1-4)
      goalDiff <= -2 -> {
        when {
          minute < 45 -> Triple(20.00, 8.50, 1.10)
          minute < 70 -> Triple(35.00, 15.00, 1.04)
          else -> Triple(60.00, 25.00, 1.02)
        }
      }
      // Draw (0-0, 1-1, 2-2)
      else -> {
        when {
          minute < 30 -> Triple(2.30, 3.10, 2.70)
          minute < 60 -> Triple(2.60, 2.70, 3.10)
          minute < 75 -> Triple(3.40, 2.05, 4.00)
          else -> Triple(4.80, 1.45, 5.50)
        }
      }
    }

    val updatedSelections = market.selections.map { sel ->
      val newOdd = when {
        sel.name.contains("1") && !sel.name.contains("X") && !sel.name.contains("2") -> rawHome
        sel.name.contains("X") || sel.name.contains("Beraberlik") -> rawDraw
        sel.name.contains("2") -> rawAway
        else -> sel.odd
      }
      val validOdd = roundOdd(max(1.02, newOdd))
      sel.copy(
        odd = validOdd,
        previousOdd = sel.odd,
        isOddsUp = validOdd > sel.odd,
        isOddsDown = validOdd < sel.odd
      )
    }

    return market.copy(
      selections = updatedSelections,
      isSuspended = isSuspended
    )
  }

  private fun calculateOverUnder(
    market: Market,
    threshold: Double,
    totalGoals: Int,
    remainingMinutes: Int,
    isSuspended: Boolean
  ): Market {
    val goalsNeeded = threshold - totalGoals

    val (altOdd, ustOdd) = when {
      goalsNeeded <= 0 -> {
        // Already more goals than threshold: Alt is BUSTED, Ust is WON
        Pair(1.00, 1.00)
      }
      goalsNeeded == 0.5 -> {
        // Need exactly 1 more goal to bust Alt
        when {
          remainingMinutes > 60 -> Pair(2.10, 1.65)
          remainingMinutes > 30 -> Pair(1.65, 2.10)
          remainingMinutes > 15 -> Pair(1.30, 3.20)
          else -> Pair(1.12, 5.50)
        }
      }
      goalsNeeded == 1.5 -> {
        // Need 2 more goals
        when {
          remainingMinutes > 60 -> Pair(1.40, 2.70)
          remainingMinutes > 30 -> Pair(1.22, 3.80)
          else -> Pair(1.08, 6.50)
        }
      }
      else -> {
        // Need 3+ goals
        Pair(1.04, 9.00)
      }
    }

    val updatedSelections = market.selections.map { sel ->
      val isAlt = sel.name.contains("Alt", ignoreCase = true)
      val newOdd = if (isAlt) altOdd else ustOdd
      val validOdd = roundOdd(max(1.01, newOdd))
      sel.copy(
        odd = validOdd,
        previousOdd = sel.odd,
        isOddsUp = validOdd > sel.odd,
        isOddsDown = validOdd < sel.odd
      )
    }

    return market.copy(
      selections = updatedSelections,
      isSuspended = isSuspended
    )
  }

  private fun calculateBothTeamsScore(
    market: Market,
    homeScore: Int,
    awayScore: Int,
    remainingMinutes: Int,
    isSuspended: Boolean
  ): Market {
    val (varOdd, yokOdd) = when {
      homeScore >= 1 && awayScore >= 1 -> Pair(1.00, 1.00) // Both scored, settled!
      homeScore == 0 && awayScore == 0 -> {
        when {
          remainingMinutes > 60 -> Pair(1.75, 1.95)
          remainingMinutes > 30 -> Pair(2.40, 1.50)
          else -> Pair(4.20, 1.18)
        }
      }
      else -> {
        // Exactly one team scored, waiting for the other
        when {
          remainingMinutes > 60 -> Pair(1.55, 2.30)
          remainingMinutes > 30 -> Pair(2.05, 1.70)
          remainingMinutes > 15 -> Pair(3.10, 1.32)
          else -> Pair(5.20, 1.12)
        }
      }
    }

    val updatedSelections = market.selections.map { sel ->
      val isVar = sel.name.contains("Var", ignoreCase = true)
      val newOdd = if (isVar) varOdd else yokOdd
      val validOdd = roundOdd(max(1.01, newOdd))
      sel.copy(
        odd = validOdd,
        previousOdd = sel.odd,
        isOddsUp = validOdd > sel.odd,
        isOddsDown = validOdd < sel.odd
      )
    }

    return market.copy(
      selections = updatedSelections,
      isSuspended = isSuspended
    )
  }

  private fun calculateDoubleChance(
    market: Market,
    goalDiff: Int,
    minute: Int,
    remainingMinutes: Int,
    isSuspended: Boolean
  ): Market {
    val (odd1X, odd12, oddX2) = when {
      goalDiff >= 1 -> Triple(1.02, 1.15, 4.50)
      goalDiff <= -1 -> Triple(4.50, 1.15, 1.02)
      else -> Triple(1.35, 1.30, 1.40)
    }

    val updatedSelections = market.selections.map { sel ->
      val newOdd = when {
        sel.name.contains("1-X") || sel.name.contains("1X") -> odd1X
        sel.name.contains("1-2") || sel.name.contains("12") -> odd12
        sel.name.contains("X-2") || sel.name.contains("X2") -> oddX2
        else -> sel.odd
      }
      val validOdd = roundOdd(max(1.01, newOdd))
      sel.copy(
        odd = validOdd,
        previousOdd = sel.odd,
        isOddsUp = validOdd > sel.odd,
        isOddsDown = validOdd < sel.odd
      )
    }

    return market.copy(
      selections = updatedSelections,
      isSuspended = isSuspended
    )
  }

  private fun calculateHandicap(
    market: Market,
    goalDiff: Int,
    minute: Int,
    isSuspended: Boolean
  ): Market {
    // Standard HMS-1: Home starts -1
    val effectiveDiff = goalDiff - 1
    val (hms1, hmsX, hms2) = when {
      effectiveDiff >= 1 -> Triple(1.10, 7.00, 18.00)
      effectiveDiff == 0 -> Triple(2.80, 2.20, 3.40)
      else -> Triple(8.00, 4.50, 1.25)
    }

    val updatedSelections = market.selections.map { sel ->
      val newOdd = when {
        sel.name.contains("1") -> hms1
        sel.name.contains("X") -> hmsX
        else -> hms2
      }
      val validOdd = roundOdd(max(1.01, newOdd))
      sel.copy(
        odd = validOdd,
        previousOdd = sel.odd,
        isOddsUp = validOdd > sel.odd,
        isOddsDown = validOdd < sel.odd
      )
    }

    return market.copy(
      selections = updatedSelections,
      isSuspended = isSuspended
    )
  }

  fun roundOdd(value: Double): Double {
    return (value * 100.0).roundToInt() / 100.0
  }
}
