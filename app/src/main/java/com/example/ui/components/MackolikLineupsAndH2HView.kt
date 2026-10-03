package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.datasource.MackolikLeagueDataSource
import com.example.data.model.Match
import com.example.data.model.PlayerLineup
import com.example.data.model.Sport
import com.example.ui.components.detail.AmericanFootballMatchDetailContent
import com.example.ui.components.detail.BasketballMatchDetailContent
import com.example.ui.components.detail.CombatMatchDetailContent
import com.example.ui.components.detail.FootballMatchDetailContent
import com.example.ui.components.detail.MotorsportDetailContent
import com.example.ui.components.detail.TennisMatchDetailContent
import com.example.ui.components.detail.VolleyballMatchDetailContent

/**
 * Authentic Multi-Sport Lineup & Branch Detail View.
 * Delegates to the exact sport-specific module (MotoGP Grid/Telemetry,
 * Basketball Court/Starting 5, Tennis Court/Sets, Combat Octagon,
 * Volleyball Court/Rotations, NFL 100-Yd Field, and Football Tactical Pitch with ALL 11 Players).
 */
@Composable
fun MackolikLineupsAndH2HView(match: Match) {
  val lineups = remember(match.id) { MackolikLeagueDataSource.getLineupsForMatch(match) }
  var showPlayerDirectory by remember { mutableStateOf(false) }
  var selectedPlayer by remember { mutableStateOf<PlayerLineup?>(null) }

  if (showPlayerDirectory) {
    MackolikPlayerDirectoryDialog(
      initialQuery = match.homeTeam,
      onDismiss = { showPlayerDirectory = false }
    )
  }

  val isMotorsport = match.sport == Sport.MOTORSPORTS ||
      match.sport == Sport.MOTOGP ||
      match.sport == Sport.FORMULA_1 ||
      match.sport == Sport.FORMULA_2 ||
      match.sport == Sport.FORMULA_3 ||
      match.sport == Sport.WRC_RALLY ||
      match.sport == Sport.NASCAR ||
      match.sport == Sport.INDYCAR ||
      match.league.contains("MotoGP", ignoreCase = true) ||
      match.league.contains("Formula", ignoreCase = true) ||
      match.league.contains("F1", ignoreCase = true) ||
      match.league.contains("WRC", ignoreCase = true) ||
      match.league.contains("NASCAR", ignoreCase = true) ||
      match.league.contains("IndyCar", ignoreCase = true)

  val isBasketball = match.sport == Sport.BASKETBALL ||
      match.sport == Sport.BASKETBALL_3X3 ||
      match.sport == Sport.E_BASKETBALL ||
      match.league.contains("Basketbol", ignoreCase = true) ||
      match.league.contains("NBA", ignoreCase = true) ||
      match.league.contains("EuroLeague", ignoreCase = true)

  val isTennis = match.sport == Sport.TENNIS ||
      match.sport == Sport.TABLE_TENNIS ||
      match.sport == Sport.PADEL ||
      match.sport == Sport.BADMINTON ||
      match.sport == Sport.SQUASH ||
      match.sport == Sport.PICKLEBALL ||
      match.league.contains("Tenis", ignoreCase = true) ||
      match.league.contains("WTA", ignoreCase = true) ||
      match.league.contains("ATP", ignoreCase = true)

  val isCombat = match.sport == Sport.MMA_UFC ||
      match.sport == Sport.BOXING ||
      match.sport == Sport.KICKBOXING ||
      match.sport == Sport.MUAY_THAI ||
      match.sport == Sport.JUDO ||
      match.sport == Sport.KARATE ||
      match.sport == Sport.TAEKWONDO ||
      match.sport == Sport.WRESTLING ||
      match.league.contains("UFC", ignoreCase = true) ||
      match.league.contains("MMA", ignoreCase = true) ||
      match.league.contains("Boks", ignoreCase = true)

  val isVolleyball = match.sport == Sport.VOLLEYBALL ||
      match.sport == Sport.BEACH_VOLLEYBALL ||
      match.league.contains("Voleybol", ignoreCase = true) ||
      match.league.contains("Sultanlar", ignoreCase = true)

  val isAmericanFootball = match.sport == Sport.AMERICAN_FOOTBALL ||
      match.sport == Sport.RUGBY_UNION ||
      match.sport == Sport.RUGBY_LEAGUE ||
      match.league.contains("NFL", ignoreCase = true)

  Box(modifier = Modifier.fillMaxSize()) {
    when {
      isMotorsport -> MotorsportDetailContent(
        match = match,
        onOpenDirectory = { showPlayerDirectory = true }
      )
      isBasketball -> BasketballMatchDetailContent(
        match = match,
        lineups = lineups,
        selectedPlayer = selectedPlayer,
        onPlayerClick = { selectedPlayer = it }
      )
      isTennis -> TennisMatchDetailContent(match = match)
      isCombat -> CombatMatchDetailContent(match = match)
      isVolleyball -> VolleyballMatchDetailContent(
        match = match,
        onOpenDirectory = { showPlayerDirectory = true }
      )
      isAmericanFootball -> AmericanFootballMatchDetailContent(
        match = match,
        onOpenDirectory = { showPlayerDirectory = true }
      )
      else -> FootballMatchDetailContent(
        lineups = lineups,
        homeTeam = match.homeTeam,
        awayTeam = match.awayTeam,
        selectedPlayer = selectedPlayer,
        onPlayerClick = { selectedPlayer = it },
        onOpenPlayerDirectory = { showPlayerDirectory = true }
      )
    }
  }
}
