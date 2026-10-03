package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketType
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SlipSelection
import com.example.data.model.Sport
import com.example.ui.components.LiveGoalBanner
import com.example.ui.components.MackolikLeagueFilterRow
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TealDark
import com.example.util.formatOdd
import com.example.util.formatXg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScreen(
  matches: List<Match>,
  selectedSelections: List<SlipSelection>,
  simulationMessage: String?,
  favoriteMatchIds: Set<String> = emptySet(),
  onToggleFavorite: (String) -> Unit = {},
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenMatchDetail: (Match) -> Unit
) {
  val liveMatches = matches.filter { it.status == MatchStatus.LIVE }
  var selectedLeague by remember { mutableStateOf("Tümü") }
  var selectedSportFilter by remember { mutableStateOf<Sport?>(null) }
  var showHighXgOnly by remember { mutableStateOf(false) }

  val displayedMatches = liveMatches.filter { match ->
    val leagueOk = selectedLeague == "Tümü" || match.league == selectedLeague
    val sportOk = selectedSportFilter == null || match.sport == selectedSportFilter
    val xgOk = !showHighXgOnly || (match.statistics.xgHome + match.statistics.xgAway) >= 1.8
    leagueOk && sportOk && xgOk
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFFF1F5F9))
      .testTag("live_matches_screen")
  ) {
    // Compact, sleek live matches header bar (replaces oversized green block)
    Surface(
      color = Color.White,
      shadowElevation = 2.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("live_matches_compact_header")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(LiveRed)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Canlı Karşılaşmalar",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = Color(0xFF0F172A)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = LiveRed.copy(alpha = 0.12f),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(
              text = "${liveMatches.size} Maç",
              color = LiveRed,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = Color(0xFF10B981).copy(alpha = 0.12f),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(5.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF10B981))
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "WebSocket Canlı",
                color = Color(0xFF059669),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          Surface(
            color = LiveRed,
            shape = RoundedCornerShape(6.dp)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Sensors,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "CANLI",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) {
      // 1. Simulation Goal Banner
      if (simulationMessage != null) {
        item {
          LiveGoalBanner(
            message = simulationMessage,
            onDismiss = {}
          )
        }
      }

      // 2. League Filter Bar
      item {
        MackolikLeagueFilterRow(
          selectedLeague = selectedLeague,
          onSelectLeague = { selectedLeague = it }
        )
      }

      // 3. Sport & Metric Filter Chips
      item {
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          item {
            FilterChip(
              selected = selectedSportFilter == null && !showHighXgOnly,
              onClick = {
                selectedSportFilter = null
                showHighXgOnly = false
              },
              label = { Text("Tümü (${liveMatches.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TealDark,
                selectedLabelColor = Color.White
              )
            )
          }

          item {
            FilterChip(
              selected = selectedSportFilter == Sport.FOOTBALL,
              onClick = {
                selectedSportFilter = if (selectedSportFilter == Sport.FOOTBALL) null else Sport.FOOTBALL
              },
              label = { Text("⚽ Futbol", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TealDark,
                selectedLabelColor = Color.White
              )
            )
          }

          item {
            FilterChip(
              selected = selectedSportFilter == Sport.BASKETBALL,
              onClick = {
                selectedSportFilter = if (selectedSportFilter == Sport.BASKETBALL) null else Sport.BASKETBALL
              },
              label = { Text("🏀 Basketbol", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = TealDark,
                selectedLabelColor = Color.White
              )
            )
          }

          item {
            FilterChip(
              selected = showHighXgOnly,
              onClick = { showHighXgOnly = !showHighXgOnly },
              label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(11.dp))
                  Spacer(modifier = Modifier.width(3.dp))
                  Text("Yüksek xG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GoldYellow,
                selectedLabelColor = TealDark
              )
            )
          }
        }
      }

      // 4. Matches List or Empty State
      if (displayedMatches.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "⏱️", fontSize = 38.sp)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Filtreye uygun canlı karşılaşma bulunamadı.",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334155),
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Bültende toplam ${liveMatches.size} adet canlı maç devam ediyor.",
                color = Color(0xFF64748B),
                fontSize = 12.sp
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = {
                  selectedLeague = "Tümü"
                  selectedSportFilter = null
                  showHighXgOnly = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealDark),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("Tüm Canlı Maçları Göster", color = Color.White, fontSize = 11.sp)
              }
            }
          }
        }
      } else {
        items(displayedMatches, key = { it.id }) { match ->
          LiveMatchItemCard(
            match = match,
            selectedSelections = selectedSelections,
            onSelectOdd = onSelectOdd,
            onOpenMatchDetail = { onOpenMatchDetail(match) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(90.dp))
      }
    }
  }
}

@Composable
fun LiveMatchItemCard(
  match: Match,
  selectedSelections: List<SlipSelection>,
  onSelectOdd: (SlipSelection) -> Unit,
  onOpenMatchDetail: () -> Unit
) {
  val msMarket = match.markets.find { it.type == MarketType.MATCH_RESULT }
  val ms1 = msMarket?.selections?.getOrNull(0)
  val msX = msMarket?.selections?.getOrNull(1)
  val ms2 = msMarket?.selections?.getOrNull(2)

  val isLiveLocked85 = match.status == MatchStatus.LIVE && match.minute >= 85
  val isMarketSuspended = (msMarket?.isSuspended ?: false) || isLiveLocked85

  val totalXg = match.statistics.xgHome + match.statistics.xgAway

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 5.dp)
      .testTag("live_match_card_${match.id}")
      .clickable { onOpenMatchDetail() },
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top row: League and Minute Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = match.league,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )
          if (match.tvBroadcast.isNotBlank()) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "📺 ${match.tvBroadcast}",
                fontSize = 9.sp,
                color = Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
        }

        // Live Minute Badge
        Surface(
          color = Color(0xFFFEE2E2),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, Color(0xFFFCA5A5))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(LiveRed)
            )
            Spacer(modifier = Modifier.width(4.dp))
            val liveBadgeText = if (match.minute >= 90 && match.currentExtraMinute > 0) "90+${match.currentExtraMinute}' CANLI" else "${match.minute}' CANLI"
            Text(
              text = liveBadgeText,
              color = LiveRed,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Teams and Score Details
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Home Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.Start
        ) {
          Text(
            text = match.homeTeam,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF0F172A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "xG: ${match.statistics.xgHome.formatXg()}",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }

        // Score Box
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TealDark)
            .padding(horizontal = 14.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "${match.homeScore} - ${match.awayScore}",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            letterSpacing = 1.sp
          )
        }

        // Away Team
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.End
        ) {
          Text(
            text = match.awayTeam,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF0F172A),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "xG: ${match.statistics.xgAway.formatXg()}",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 2D Live Pitch Simulator Action
      Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenMatchDetail() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val (simIcon, simLabel) = when {
            match.sport == Sport.TENNIS || match.league.contains("ATP", ignoreCase = true) || match.league.contains("WTA", ignoreCase = true) || match.league.contains("Tenis", ignoreCase = true) -> "🎾" to "2D Canlı Tenis Kort Simülatörü"
            match.sport == Sport.BASKETBALL -> "🏀" to "2D Canlı Basketbol Simülatörü"
            match.sport == Sport.VOLLEYBALL -> "🏐" to "2D Canlı Voleybol Simülatörü"
            match.sport == Sport.ICE_HOCKEY -> "🏒" to "2D Canlı Buz Hokeyi Simülatörü"
            match.sport == Sport.TABLE_TENNIS -> "🏓" to "2D Canlı Masa Tenisi Simülatörü"
            match.sport == Sport.BILLIARDS || match.sport == Sport.SNOOKER -> "🎱" to "2D Canlı Bilardo Masası"
            match.sport == Sport.OKEY_CARDS -> "🀄" to "2D Canlı Okey Masası"
            match.sport == Sport.MOTORSPORTS || match.league.contains("WRC", ignoreCase = true) || match.league.contains("MotoGP", ignoreCase = true) -> "🏎️" to "2D Canlı Yarış & Telemetri"
            else -> "🏟️" to "2D Canlı Futbol Sahası (Maçkolik)"
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = simIcon, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = simLabel,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
          Text(
            text = "İzle ➔",
            color = GoldYellow,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Odds and 'Bet Now' Button Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // MS 1
        if (ms1 != null) {
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == ms1.id }
          LiveOddPill(
            label = "1",
            odd = ms1.odd,
            isSelected = isSelected,
            isSuspended = isMarketSuspended,
            modifier = Modifier.weight(1f),
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = MarketType.MATCH_RESULT,
                  selectionId = ms1.id,
                  selectionName = "1",
                  odd = ms1.odd,
                  isLive = true
                )
              )
            }
          )
        }

        // MS X
        if (msX != null) {
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == msX.id }
          LiveOddPill(
            label = "X",
            odd = msX.odd,
            isSelected = isSelected,
            isSuspended = isMarketSuspended,
            modifier = Modifier.weight(1f),
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = MarketType.MATCH_RESULT,
                  selectionId = msX.id,
                  selectionName = "X",
                  odd = msX.odd,
                  isLive = true
                )
              )
            }
          )
        }

        // MS 2
        if (ms2 != null) {
          val isSelected = selectedSelections.any { it.matchId == match.id && it.selectionId == ms2.id }
          LiveOddPill(
            label = "2",
            odd = ms2.odd,
            isSelected = isSelected,
            isSuspended = isMarketSuspended,
            modifier = Modifier.weight(1f),
            onClick = {
              onSelectOdd(
                SlipSelection(
                  matchId = match.id,
                  matchTeams = "${match.homeTeam} - ${match.awayTeam}",
                  marketType = MarketType.MATCH_RESULT,
                  selectionId = ms2.id,
                  selectionName = "2",
                  odd = ms2.odd,
                  isLive = true
                )
              )
            }
          )
        }

        // Dedicated 'Bet Now' ("Hemen Oyna") Button
        Button(
          onClick = onOpenMatchDetail,
          modifier = Modifier
            .height(38.dp)
            .testTag("bet_now_button_${match.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = GoldYellow),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = TealDark,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = if (isLiveLocked85) "Kilitli 🔒" else "Hemen Oyna",
            color = TealDark,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
private fun LiveOddPill(
  label: String,
  odd: Double,
  isSelected: Boolean,
  isSuspended: Boolean = false,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .height(38.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) GoldYellow else Color(0xFFF1F5F9))
      .border(
        width = 1.dp,
        color = if (isSelected) TealDark else Color(0xFFE2E8F0),
        shape = RoundedCornerShape(8.dp)
      )
      .clickable(enabled = !isSuspended) { onClick() }
      .testTag("live_odd_${label}_${odd}"),
    contentAlignment = Alignment.Center
  ) {
    if (isSuspended) {
      Text(
        text = "🔒",
        fontSize = 12.sp
      )
    } else {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = label,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = if (isSelected) TealDark else Color(0xFF64748B)
        )
        Text(
          text = odd.formatOdd(),
          fontSize = 11.sp,
          fontWeight = FontWeight.Black,
          color = if (isSelected) TealDark else Color(0xFF0F172A)
        )
      }
    }
  }
}
