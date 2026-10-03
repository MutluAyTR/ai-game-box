package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.Sport
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import kotlinx.coroutines.delay

/**
 * Real-Time Live Score Dashboard Component:
 * Displays ongoing matches across major leagues with pulsating current minute markers,
 * score updates, and live match cards.
 */
@Composable
fun RealTimeLiveScoreDashboardComponent(
  matches: List<Match>,
  onMatchClick: (Match) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedLeagueFilter by remember { mutableStateOf("Tümü") }

  // Pulsing animation for active live indicators
  val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val majorLeagues = remember {
    listOf("Tümü", "Süper Lig", "Premier League", "La Liga", "EuroLeague", "Motorspor")
  }

  // Filter live matches or matches with active scores
  val liveMatches = remember(matches, selectedLeagueFilter) {
    matches.filter { m ->
      val isLiveOrSimulated = m.status == MatchStatus.LIVE || m.minute > 0
      val matchesLeague = when (selectedLeagueFilter) {
        "Süper Lig" -> m.league.contains("Süper", ignoreCase = true)
        "Premier League" -> m.league.contains("Premier", ignoreCase = true)
        "La Liga" -> m.league.contains("La Liga", ignoreCase = true)
        "EuroLeague" -> m.league.contains("EuroLeague", ignoreCase = true) || m.sport == Sport.BASKETBALL
        "Motorspor" -> m.sport == Sport.MOTORSPORTS || m.league.contains("MotoGP", ignoreCase = true) || m.league.contains("WRC", ignoreCase = true)
        else -> true
      }
      isLiveOrSimulated && matchesLeague
    }
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
    border = BorderStroke(1.dp, Color(0xFF1E293B)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("real_time_live_score_dashboard")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // 1. Dashboard Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(LiveRed)
              .alpha(pulseAlpha)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CANLI SKOR DASHBOARD",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = LiveRed,
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "${liveMatches.size} CANLI",
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
        }

        Text(
          text = "Maçkolik Anlık Feed",
          color = Color(0xFF94A3B8),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. League Filter Chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        majorLeagues.forEach { league ->
          val isSel = selectedLeagueFilter == league
          Surface(
            color = if (isSel) TealDark else Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (isSel) GoldYellow else Color(0xFF334155)),
            modifier = Modifier.clickable { selectedLeagueFilter = league }
          ) {
            Text(
              text = league,
              color = if (isSel) GoldYellow else Color(0xFFCBD5E1),
              fontSize = 10.sp,
              fontWeight = if (isSel) FontWeight.Black else FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Horizontal Scrollable Live Matches Cards
      if (liveMatches.isEmpty()) {
        Surface(
          color = Color(0xFF1E293B),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Seçilen ligde şu anda devam eden canlı maç bulunmuyor.",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            modifier = Modifier.padding(12.dp)
          )
        }
      } else {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(liveMatches, key = { it.id }) { match ->
            LiveScoreMatchCard(
              match = match,
              pulseAlpha = pulseAlpha,
              onClick = { onMatchClick(match) }
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LiveScoreMatchCard(
  match: Match,
  pulseAlpha: Float,
  onClick: () -> Unit
) {
  // Live ticking minute for dashboard cards
  var dynamicMinute by remember(match.id, match.minute) {
    mutableIntStateOf(if (match.minute > 0) match.minute else 48)
  }
  var extraMinute by remember(match.id, match.currentExtraMinute) {
    mutableIntStateOf(match.currentExtraMinute)
  }
  var isFinished by remember(match.id, match.status) {
    mutableStateOf(match.status == MatchStatus.FINISHED)
  }

  LaunchedEffect(match.id, isFinished) {
    while (!isFinished) {
      delay(3000L)
      if (dynamicMinute < 90) {
        dynamicMinute++
      } else {
        if (extraMinute < 5) {
          extraMinute++
        } else {
          isFinished = true
        }
      }
    }
  }

  Surface(
    color = Color(0xFF1E293B),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, Color(0xFF334155)),
    modifier = Modifier
      .width(210.dp)
      .clickable { onClick() }
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Header: League & Pulsing Minute Marker
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = match.league,
          color = Color(0xFF94A3B8),
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        Surface(
          color = Color(0xFF450A0A),
          shape = RoundedCornerShape(4.dp),
          border = BorderStroke(1.dp, LiveRed)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(LiveRed)
                .alpha(pulseAlpha)
            )
            Spacer(modifier = Modifier.width(3.dp))
            val isMotorsport = match.sport == Sport.MOTORSPORTS ||
                match.league.contains("MotoGP", ignoreCase = true) ||
                match.league.contains("WRC", ignoreCase = true)

            val badgeText = when {
              isFinished -> "MS"
              isMotorsport -> {
                val label = if (match.league.contains("WRC", ignoreCase = true)) "Etap" else "Tur"
                "$label ${dynamicMinute.coerceIn(1, 26)}"
              }
              dynamicMinute >= 90 && extraMinute > 0 -> "90+$extraMinute'"
              else -> "$dynamicMinute'"
            }
            Text(
              text = badgeText,
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      val isMotorsport = match.sport == Sport.MOTORSPORTS ||
          match.league.contains("MotoGP", ignoreCase = true) ||
          match.league.contains("WRC", ignoreCase = true)

      // Home Team & Score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = match.homeTeam,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        if (isMotorsport) {
          Surface(
            color = if (match.homeScore >= match.awayScore) Color(0xFFB45309) else Color(0xFF1E293B),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(0.8.dp, GoldYellow)
          ) {
            Text(
              text = "${match.homeScore} P",
              color = GoldYellow,
              fontWeight = FontWeight.Black,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
        } else {
          Text(
            text = "${match.homeScore}",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(3.dp))

      // Away Team & Score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = match.awayTeam,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )
        if (isMotorsport) {
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(0.8.dp, Color(0xFF94A3B8))
          ) {
            Text(
              text = "${match.awayScore} P",
              color = Color(0xFFE2E8F0),
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
            )
          }
        } else {
          Text(
            text = "${match.awayScore}",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Bottom Row: TV & Quick Action Hint
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "📺 ${match.tvBroadcast.ifBlank { "beIN SPORTS" }}",
          color = Color(0xFF64748B),
          fontSize = 9.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Detay & AI",
            color = Color(0xFF38BDF8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
        }
      }
    }
  }
}
